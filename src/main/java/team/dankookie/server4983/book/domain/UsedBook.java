package team.dankookie.server4983.book.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.constant.College;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.book.dto.UsedBookSaveRequest;
import team.dankookie.server4983.common.domain.BaseEntity;
import team.dankookie.server4983.member.domain.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UsedBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer price;

    @NotNull
    private LocalDateTime tradeAvailableDatetime;

    private String publisher;

    @Enumerated(EnumType.STRING)
    private College college;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Setter
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @ColumnDefault("false")
    private Boolean isUnderlinedOrWrite;

    @ColumnDefault("false")
    private Boolean isDiscolorationAndDamage;

    @ColumnDefault("false")
    private Boolean isCoverDamaged;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Member buyerMember;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member sellerMember;

    @ColumnDefault("false")
    private Boolean isDeleted;

    @ToString.Exclude
    @OneToMany(mappedBy = "usedBook", cascade = CascadeType.ALL)
    private List<BookImage> bookImageList = new ArrayList<>();

    public void setIsDeletedTrue() {
        isDeleted = true;
    }

    @Builder
    public UsedBook(Long id, String name, Integer price, LocalDateTime tradeAvailableDatetime, String publisher, College college, Department department, BookStatus bookStatus, Boolean isUnderlinedOrWrite, Boolean isDiscolorationAndDamage, Boolean isCoverDamaged, Member buyerMember, Member sellerMember, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.tradeAvailableDatetime = tradeAvailableDatetime;
        this.publisher = publisher;
        this.college = college;
        this.department = department;
        this.bookStatus = bookStatus;
        this.isUnderlinedOrWrite = isUnderlinedOrWrite;
        this.isDiscolorationAndDamage = isDiscolorationAndDamage;
        this.isCoverDamaged = isCoverDamaged;
        this.buyerMember = buyerMember;
        this.sellerMember = sellerMember;
        this.isDeleted = isDeleted;
    }

    public void updateUsedBook(UsedBookSaveRequest usedBook) {
        this.name = usedBook.name();
        this.price = usedBook.price();
        this.tradeAvailableDatetime = usedBook.tradeAvailableDatetime();
        this.publisher = usedBook.publisher();
        this.college = usedBook.college();
        this.department = usedBook.department();
        this.isUnderlinedOrWrite = usedBook.isUnderlinedOrWrite();
        this.isDiscolorationAndDamage = usedBook.isDiscolorationAndDamage();
        this.isCoverDamaged = usedBook.isCoverDamaged();
    }
}
