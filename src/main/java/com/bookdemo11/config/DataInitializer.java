package com.bookdemo11.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bookdemo11.article.entity.Article;
import com.bookdemo11.article.entity.News;
import com.bookdemo11.article.entity.Review;
import com.bookdemo11.article.repository.ArticleRepository;
import com.bookdemo11.article.repository.NewsRepository;
import com.bookdemo11.article.repository.ReviewRepository;
import com.bookdemo11.coupon.entity.Coupon;
import com.bookdemo11.coupon.repository.CouponRepository;
import com.bookdemo11.employee.entity.Department;
import com.bookdemo11.employee.entity.Employee;
import com.bookdemo11.employee.entity.Gender;
import com.bookdemo11.employee.entity.JobTitle;
import com.bookdemo11.employee.repository.DepartmentRepository;
import com.bookdemo11.employee.repository.EmployeeRepository;
import com.bookdemo11.employee.repository.JobTitleRepository;
import com.bookdemo11.member.entity.Member;
import com.bookdemo11.member.repository.MemberRepository;
import com.bookdemo11.room.entity.Room;
import com.bookdemo11.room.entity.RoomType;
import com.bookdemo11.room.repository.RoomRepository;
import com.bookdemo11.room.repository.RoomTypeRepository;
import com.bookdemo11.shop.entity.Product;
import com.bookdemo11.shop.repository.ProductRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final JobTitleRepository jobTitleRepository;
    private final NewsRepository newsRepository;
    private final ArticleRepository articleRepository;
    private final ReviewRepository reviewRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(MemberRepository memberRepository,
                           EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           JobTitleRepository jobTitleRepository,
                           NewsRepository newsRepository,
                           ArticleRepository articleRepository,
                           ReviewRepository reviewRepository,
                           RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository,
                           CouponRepository couponRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.newsRepository = newsRepository;
        this.articleRepository = articleRepository;
        this.reviewRepository = reviewRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            return;
        }

        Member demoMember = new Member();
        demoMember.setMemberName("測試會員");
        demoMember.setMemberEmail("member@test.com");
        demoMember.setMemberPassword(passwordEncoder.encode("123456"));
        demoMember.setMemberPhone("0912345678");
        demoMember.setMemberAddress("台北市信義區");
        memberRepository.save(demoMember);

        Department itDept = createDepartment("資訊部");
        Department frontDeskDept = createDepartment("櫃檯部");
        Department housekeepingDept = createDepartment("房務部");
        Department marketingDept = createDepartment("行銷部");

        JobTitle adminTitle = createJobTitle("系統管理員");
        JobTitle frontDeskTitle = createJobTitle("櫃檯專員");
        JobTitle housekeepingTitle = createJobTitle("房務主管");
        JobTitle marketingTitle = createJobTitle("行銷專員");

        Employee admin = createEmployee("系統管理員", "admin@thestar.com", "admin123",
                "02-1234-5678", "台北市信義區", itDept, adminTitle, Gender.M);
        Employee staff = createEmployee("櫃檯人員", "staff@thestar.com", "staff123",
                "08-888-8888", "屏東縣恆春鎮", frontDeskDept, frontDeskTitle, Gender.F);
        Employee housekeeping = createEmployee("房務主管", "hk@thestar.com", "hk123456",
                "08-777-7777", "屏東縣恆春鎮", housekeepingDept, housekeepingTitle, Gender.M);
        createEmployee("行銷專員", "marketing@thestar.com", "mkt12345",
                "08-666-6666", "屏東縣恆春鎮", marketingDept, marketingTitle, Gender.F);

        News news = new News();
        news.setTitle("The Star 開幕優惠");
        news.setContent("歡迎蒞臨 The Star 海景度假酒店，即日起至月底訂房享 9 折優惠。");
        news.setViewCount(128);
        newsRepository.save(news);

        Article article = new Article();
        article.setEmployee(admin);
        article.setCategory("旅遊攻略");
        article.setTitle("墾丁三天兩夜慢活行程");
        article.setContent("Day1 入住 The Star、夕陽沙滩散步；Day2 水上活動與夜遊；Day3 SPA 後返程。");
        article.setCoverImage(new byte[]{0});
        article.setViewCount(56);
        article.setStatus((byte) 1);
        articleRepository.save(article);

        Review review = new Review();
        review.setArticle(article);
        review.setMember(demoMember);
        review.setContent("行程規劃很實用，封面照片也很美！");
        review.setLikeCount(3);
        reviewRepository.save(review);

        RoomType deluxe = createRoomType("海景雙人房",
                "面海落地窗，附陽台與獨立浴缸，適合情侶與小家庭度假。",
                6800, 2);
        RoomType family = createRoomType("家庭四人房",
                "兩大床設計，寬敞空間與兒童友善設施，適合親子旅遊。",
                8800, 4);
        RoomType suite = createRoomType("總統套房",
                "頂樓全景視野，客廳與臥室分離，附管家服務與迎賓禮。",
                15800, 4);

        createRooms(deluxe, "A", 3, 5);
        createRooms(family, "B", 5, 4);
        createRooms(suite, "C", 8, 2);

        Coupon coupon = new Coupon();
        coupon.setCouponCode("STAR500");
        coupon.setCouponName("新會員折抵券");
        coupon.setDiscountAmount(500);
        coupon.setMinAmount(3000);
        coupon.setStartDate(LocalDate.now().minusDays(30));
        coupon.setEndDate(LocalDate.now().plusMonths(6));
        couponRepository.save(coupon);

        createProduct("墾丁手工皂禮盒", "天然精油手工皂 6 入組，適合送禮", 680, 50);
        createProduct("海景明信片組", "The Star 限定攝影明信片 12 張", 280, 100);
        createProduct("SPA 體驗券", "60 分鐘泰式芳療按摩兌換券", 1800, 30);
        createProduct("墾丁紅茶禮罐", "在地小農紅茶，附精美鐵罐包裝", 450, 80);
    }

    private Department createDepartment(String name) {
        Department dept = new Department();
        dept.setDepartmentName(name);
        dept.setStatus((byte) 1);
        return departmentRepository.save(dept);
    }

    private JobTitle createJobTitle(String name) {
        JobTitle title = new JobTitle();
        title.setJobTitleName(name);
        title.setStatus((byte) 1);
        return jobTitleRepository.save(title);
    }

    private Employee createEmployee(String name, String mail, String rawPassword, String phone,
                                    String address, Department department, JobTitle jobTitle, Gender gender) {
        Employee employee = new Employee();
        employee.setEmployeeName(name);
        employee.setEmployeeMail(mail);
        employee.setEmployeePassword(passwordEncoder.encode(rawPassword));
        employee.setPhone(phone);
        employee.setAddress(address);
        employee.setDepartment(department);
        employee.setJobTitle(jobTitle);
        employee.setGender(gender);
        employee.setHireDate(LocalDate.now());
        employee.setStatus((byte) 1);
        return employeeRepository.save(employee);
    }

    private void createProduct(String name, String desc, int price, int stock) {
        Product product = new Product();
        product.setProductName(name);
        product.setProductDesc(desc);
        product.setProductPrice(price);
        product.setStock(stock);
        product.setSaleStatus(1);
        productRepository.save(product);
    }

    private RoomType createRoomType(String name, String content, int price, int guests) {
        RoomType rt = new RoomType();
        rt.setRoomTypeName(name);
        rt.setRoomTypeContent(content);
        rt.setRoomTypePrice(price);
        rt.setGuestNum(guests);
        rt.setRoomSaleStatus((byte) 1);
        return roomTypeRepository.save(rt);
    }

    private void createRooms(RoomType roomType, String floorPrefix, int floor, int count) {
        for (int i = 1; i <= count; i++) {
            Room room = new Room();
            room.setRoomName(floorPrefix + String.format("%02d", i));
            room.setFloorNum(floor);
            room.setRoomStatus(1);
            room.setRoomType(roomType);
            roomRepository.save(room);
        }
    }
}