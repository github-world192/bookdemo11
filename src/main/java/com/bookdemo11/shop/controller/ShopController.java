package com.bookdemo11.shop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;
import com.bookdemo11.payment.service.StripePaymentService;
import com.bookdemo11.payment.service.StripePaymentService.LineItem;
import com.bookdemo11.shop.dto.PendingShopOrder;
import com.bookdemo11.shop.entity.Product;
import com.bookdemo11.shop.entity.ShopOrder;
import com.bookdemo11.shop.repository.ProductRepository;
import com.bookdemo11.shop.service.ShopCartService;
import com.bookdemo11.shop.service.ShopOrderService;
import com.bookdemo11.shop.service.ShopOrderService.CheckoutLine;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/shop")
public class ShopController {

    private static final String PENDING_SHOP_KEY = "pendingShopOrder";

    private final ProductRepository productRepository;
    private final ShopCartService shopCartService;
    private final ShopOrderService shopOrderService;
    private final MemberRepository memberRepository;
    private final StripePaymentService stripePaymentService;

    public ShopController(ProductRepository productRepository,
                          ShopCartService shopCartService,
                          ShopOrderService shopOrderService,
                          MemberRepository memberRepository,
                          StripePaymentService stripePaymentService) {
        this.productRepository = productRepository;
        this.shopCartService = shopCartService;
        this.shopOrderService = shopOrderService;
        this.memberRepository = memberRepository;
        this.stripePaymentService = stripePaymentService;
    }

    @GetMapping
    public String shopIndex(Model model) {
        model.addAttribute("products", productRepository.findBySaleStatus(1));
        return "shop/index";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            shopCartService.addToCart(session, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "已加入購物車");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        Map<Integer, Integer> cart = shopCartService.getCart(session);
        List<CartView> cartViews = new ArrayList<>();
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product p = productRepository.findById(entry.getKey()).orElse(null);
            if (p != null) {
                cartViews.add(new CartView(p, entry.getValue()));
                total += p.getProductPrice() * entry.getValue();
            }
        }
        model.addAttribute("cartItems", cartViews);
        model.addAttribute("cartTotal", total);
        model.addAttribute("stripeEnabled", stripePaymentService.isConfigured());
        return "shop/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Integer productId,
                             @RequestParam Integer quantity,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            shopCartService.updateQuantity(session, productId, quantity);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/shop/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam String recipientName,
                           @RequestParam String recipientPhone,
                           @RequestParam String recipientAddress,
                           @RequestParam(defaultValue = "0") String payMethod,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();
        Map<Integer, Integer> cart = shopCartService.getCart(session);

        try {
            if ("1".equals(payMethod)) {
                return startStripeCheckout(member, cart, recipientName, recipientPhone,
                        recipientAddress, session, redirectAttributes);
            }

            ShopOrder order = shopOrderService.createOrder(member, cart, recipientName,
                    recipientPhone, recipientAddress, payMethod, null, false);
            shopCartService.clearCart(session);
            redirectAttributes.addFlashAttribute("order", order);
            return "redirect:/shop/success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/shop/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stripe 付款啟動失敗：" + e.getMessage());
            return "redirect:/shop/cart";
        }
    }

    @GetMapping("/stripe/success")
    public String stripeSuccess(@RequestParam("session_id") String sessionId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpSession httpSession,
                                RedirectAttributes redirectAttributes) {
        try {
            Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();

            if (shopOrderService.findByStripeSessionId(sessionId).isPresent()) {
                redirectAttributes.addFlashAttribute("order",
                        shopOrderService.findByStripeSessionId(sessionId).get());
                return "redirect:/shop/success";
            }

            Session session = stripePaymentService.retrieveSession(sessionId);
            if (!stripePaymentService.isSessionPaid(session)) {
                redirectAttributes.addFlashAttribute("errorMessage", "付款尚未完成");
                return "redirect:/shop/orders";
            }

            PendingShopOrder pending = (PendingShopOrder) httpSession.getAttribute(PENDING_SHOP_KEY);
            if (pending == null || !member.getMemberId().equals(pending.getMemberId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "訂單資料已過期，請重新下單");
                return "redirect:/shop/cart";
            }

            ShopOrder order = shopOrderService.createOrderFromPending(member, pending, sessionId);
            shopCartService.clearCart(httpSession);
            httpSession.removeAttribute(PENDING_SHOP_KEY);
            redirectAttributes.addFlashAttribute("order", order);
            return "redirect:/shop/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "付款確認失敗：" + e.getMessage());
            return "redirect:/shop/orders";
        }
    }

    @GetMapping("/stripe/cancel")
    public String stripeCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "已取消 Stripe 付款");
        return "redirect:/shop/cart";
    }

    @GetMapping("/success")
    public String success(Model model) {
        if (!model.containsAttribute("order")) {
            return "redirect:/shop/orders";
        }
        return "shop/success";
    }

    @GetMapping("/orders")
    public String orders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Member member = memberRepository.findByMemberEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("orders", shopOrderService.findByMember(member.getMemberId()));
        return "shop/orders";
    }

    private String startStripeCheckout(Member member, Map<Integer, Integer> cart,
                                       String recipientName, String recipientPhone,
                                       String recipientAddress, HttpSession session,
                                       RedirectAttributes redirectAttributes) throws Exception {
        List<CheckoutLine> lines = shopOrderService.buildCheckoutLines(cart);

        PendingShopOrder pending = new PendingShopOrder();
        pending.setMemberId(member.getMemberId());
        pending.setCart(new java.util.LinkedHashMap<>(cart));
        pending.setRecipientName(recipientName);
        pending.setRecipientPhone(recipientPhone);
        pending.setRecipientAddress(recipientAddress);
        pending.setPayMethod("1");
        session.setAttribute(PENDING_SHOP_KEY, pending);

        List<LineItem> stripeLines = lines.stream()
                .map(l -> new LineItem(
                        l.product().getProductName(),
                        l.product().getProductDesc(),
                        l.product().getProductPrice(),
                        l.quantity()))
                .toList();

        Session checkout = stripePaymentService.createCheckoutSession(
                "shop",
                "/shop/stripe/success",
                "/shop/stripe/cancel",
                member.getMemberEmail(),
                stripeLines,
                Map.of("member_id", String.valueOf(member.getMemberId())));

        return "redirect:" + checkout.getUrl();
    }

    public record CartView(Product product, int quantity) {}
}