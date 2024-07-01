package team.dankookie.server4983.book.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum College {
    GE("교양"),
    BUSINESS_AND_ECONOMICS("경영경제대학"),
    SW_CONVERGENCE("SW융합대학"),
    SOCIAL_SCIENCES("사회과학대학"),
    LITERAL_ARTS("문과대학"),
    LAW("법과대학"),
    ENGINEERING("공과대학"),
    EDUCATION("사범대학"),
    MUSIC_AND_ARTS("음악예술대학");

    private final String koName;
}