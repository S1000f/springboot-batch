package my.learning.springbootbatch.domain.enums;

public enum SocialType {

    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final String name;

    SocialType(String name) {
        this.name = name;
    }

    public String getRoleType() {
        String ROLE_PREFIX = "ROLE_";
        return ROLE_PREFIX + name.toUpperCase();
    }

    public String getValue() {
        return name;
    }

    public boolean isEquals(String authority) {
        return this.getRoleType().equals(authority);
    }

}
