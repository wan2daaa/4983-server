package team.dankookie.server4983.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.dankookie.server4983.book.domain.BookImage;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.dto.UsedBookResponse;
import team.dankookie.server4983.book.dto.UsedBookSaveRequest;
import team.dankookie.server4983.book.dto.UsedBookSaveResponse;
import team.dankookie.server4983.book.repository.bookImage.BookImageRepository;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;
import team.dankookie.server4983.jwt.constants.TokenSecretKey;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.s3.dto.S3Response;
import team.dankookie.server4983.s3.service.S3UploadService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsedBookService {

    private final S3UploadService uploadService;
    private final UsedBookRepository usedBookRepository;
    private final BookImageRepository bookImageRepository;
    private final MemberService memberService;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenSecretKey tokenSecretKey;

    @Transactional
    public UsedBookSaveResponse saveAndSaveFiles(List<MultipartFile> multipartFileList,
                                                 UsedBookSaveRequest usedBookSaveRequest, AccessToken accessToken) {

        String nickname = getNicknameWithAccessToken(accessToken);
        Member member = memberService.getMemberByNickname(nickname);

        UsedBook usedBook = usedBookRepository.save(usedBookSaveRequest.toEntity(member));

        for (MultipartFile multipartFile : multipartFileList) {
            S3Response s3Response = uploadService.saveFileWithUUID(multipartFile);
            BookImage bookImage = BookImage.builder()
                    .usedBook(usedBook)
                    .imageUrl(s3Response.s3ImageUrl()).build();
            bookImageRepository.save(bookImage);
        }

        return UsedBookSaveResponse.of(usedBook.getId());
    }

    public UsedBookResponse findByUsedBookIdMatchNickname(Long id, String nickname) {

        UsedBook usedBook = getUsedBookById(id);

        Member requestMember = memberService.getMemberByNickname(nickname);

        boolean isBookOwner = usedBook.getSellerMember().equals(requestMember);

        List<BookImage> bookImageList = bookImageRepository.findByUsedBook(usedBook);

        List<String> bookImageUrlList = bookImageList.stream()
                .map(BookImage::getImageUrl)
                .toList();

        return UsedBookResponse.of(
                usedBook.getCollege().name(),
                usedBook.getDepartment().name(),
                usedBook.getSellerMember().getNickname(),
                usedBook.getSellerMember().getImageUrl(),
                usedBook.getCreatedAt(),
                bookImageUrlList,
                usedBook.getName(),
                usedBook.getPublisher(),
                usedBook.getTradeAvailableDatetime(),
                usedBook.getIsUnderlinedOrWrite(),
                usedBook.getIsDiscolorationAndDamage(),
                usedBook.getIsCoverDamaged(),
                usedBook.getPrice(),
                usedBook.getBookStatus(),
                isBookOwner
        );
    }

    @Transactional
    public boolean deleteUsedBook(Long id, AccessToken accessToken) {

        String nickname = getNicknameWithAccessToken(accessToken);

        Member member = memberService.getMemberByNickname(nickname);

        boolean isUsedBookSavedByThisMember = usedBookRepository.existsUsedBookByIdAndSellerMember(id,
                member);

        if (!isUsedBookSavedByThisMember) {
            throw new IllegalArgumentException("글을 올린 사용자만 삭제할 수 있습니다.");
        }

        UsedBook usedBook = getUsedBookById(id);
        usedBook.setIsDeletedTrue();

        return true;
    }

    @Transactional
    public boolean deleteUsedBookImage(Long id, String image) {
        UsedBook usedBook = getUsedBookById(id);

        String imageUrl = uploadService.s3Bucket + image;

        long deleteCount = bookImageRepository.deleteBookImageByUsedBookAndImageUrl(usedBook, imageUrl);
        if (deleteCount == 0) {
            return false;
        }

        uploadService.deleteFile(image);
        return true;
    }

    @Transactional
    public UsedBookSaveResponse updateUsedBook(Long id, List<MultipartFile> multipartFileList,
                                               UsedBookSaveRequest usedBookSaveRequest, AccessToken accessToken) {
        String nickname = getNicknameWithAccessToken(accessToken);
        Member member = memberService.getMemberByNickname(nickname);

        UsedBook usedBook = getUsedBookById(id);

        if (!usedBook.getSellerMember().equals(member)) {
            throw new IllegalArgumentException("글을 올린 유저만 수정할 수 있습니다.");
        }

        usedBook.updateUsedBook(usedBookSaveRequest);

        if (multipartFileList != null) {
            for (MultipartFile multipartFile : multipartFileList) {
                S3Response s3Response = uploadService.saveFileWithUUID(multipartFile);
                BookImage bookImage = BookImage.builder()
                        .usedBook(usedBook)
                        .imageUrl(s3Response.s3ImageUrl()).build();
                bookImageRepository.save(bookImage);
            }
        }

        return UsedBookSaveResponse.of(usedBook.getId());
    }

    private String getNicknameWithAccessToken(AccessToken accessToken) {
        return jwtTokenUtils.getNickname(accessToken.value());
    }

    public UsedBook getUsedBookById(Long id) {
        return usedBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("id와 일치하는 중고책이 존재하지 않습니다."));
    }
}
