/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.member.configuration;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.dragonmc.core.common.member.Member;

public class LoginConfiguration {
    private transient Member member;
    private AccountType accountType;
    private String password;
    private boolean logged;
    private boolean captcha;
    private Map<String, Long> sessionMap;
    private Map<String, Long> timeoutMap;
    private Map<String, Integer> attempsMap;

    public LoginConfiguration(Member member, AccountType accountType) {
        this.member = member;
        this.accountType = accountType;
        this.password = "";
        this.logged = false;
        this.sessionMap = new HashMap<String, Long>();
        this.timeoutMap = new HashMap<String, Long>();
        this.attempsMap = new HashMap<String, Integer>();
    }

    public LoginConfiguration(Member member) {
        this(member, AccountType.PREMIUM);
    }

    public void setCaptcha(boolean captcha) {
        this.captcha = captcha;
        this.save();
    }

    public boolean isLogged() {
        return this.accountType == AccountType.PREMIUM ? true : this.logged;
    }

    public boolean reloadSession() {
        if (this.member.getIpAddress() != null && this.sessionMap.containsKey(this.member.getIpAddress())) {
            if (this.sessionMap.get(this.member.getIpAddress()) > System.currentTimeMillis()) {
                this.logged = true;
                this.member.sendMessage(this.member.getLanguage().t("login.message.session-restored", new String[0]));
                this.member.sendTitle(this.member.getLanguage().t("login.title.session-restored", new String[0]), this.member.getLanguage().t("login.subtitle.session-restored", new String[0]), 10, 40, 10);
                this.save();
                return true;
            }
            this.logged = false;
            this.member.sendMessage(this.member.getLanguage().t("login.message.session-expired", new String[0]));
            this.sessionMap.remove(this.member.getIpAddress());
            this.save();
        }
        return false;
    }

    public void startSession() {
        if (this.member.getIpAddress() != null) {
            this.sessionMap.put(this.member.getIpAddress(), System.currentTimeMillis() + 604800000L);
            this.member.sendMessage(this.member.getLanguage().t("login.message.session-created", new String[0]));
            this.save();
        }
    }

    public void stopSession() {
        if (this.member.getIpAddress() != null && this.sessionMap.containsKey(this.member.getIpAddress())) {
            this.sessionMap.remove(this.member.getIpAddress());
            this.member.sendMessage(this.member.getLanguage().t("login.message.session-stopped", new String[0]));
            this.save();
        }
    }

    public void logIn() {
        if (this.member.getIpAddress() != null) {
            this.timeoutMap.remove(this.member.getIpAddress());
            this.attempsMap.remove(this.member.getIpAddress());
        }
        this.logged = true;
        this.save();
    }

    public void logOut() {
        this.logged = false;
        this.save();
    }

    public void register(String password) {
        this.password = LoginConfiguration.encode(password);
        this.logIn();
        this.save();
    }

    public boolean isTimeouted() {
        return this.member.getIpAddress() != null ? this.timeoutMap.containsKey(this.member.getIpAddress()) && this.timeoutMap.get(this.member.getIpAddress()) > System.currentTimeMillis() : false;
    }

    public long getTimeoutTime() {
        if (this.member.getIpAddress() != null) {
            return this.timeoutMap.get(this.member.getIpAddress());
        }
        return 0L;
    }

    public Integer attemp() {
        if (this.member.getIpAddress() != null) {
            Integer integer = this.attempsMap.computeIfAbsent(this.member.getIpAddress(), v -> 0);
            integer = integer + 1;
            this.attempsMap.put(this.member.getIpAddress(), integer);
            if (integer >= 5) {
                this.attempsMap.remove(this.member.getIpAddress());
                this.timeoutMap.put(this.member.getIpAddress(), System.currentTimeMillis() + 900000L);
            }
            this.save();
            return integer;
        }
        return 0;
    }

    public boolean isPassword(String string) {
        return this.isRegistered() ? LoginConfiguration.decode(this.password).equals(string) : false;
    }

    public boolean isRegistered() {
        return this.password != null && !this.password.isEmpty();
    }

    public void loadConfiguration(Member member) {
        this.member = member;
        Iterator<Map.Entry<String, Long>> iterator = this.timeoutMap.entrySet().iterator();
        boolean save = false;
        while (iterator.hasNext()) {
            Map.Entry<String, Long> next = iterator.next();
            if (next.getValue() >= System.currentTimeMillis()) continue;
            iterator.remove();
            save = true;
        }
        if (save) {
            this.save();
        }
    }

    public void setAccountType(AccountType accountType) {
        if (this.accountType == AccountType.NONE) {
            this.accountType = accountType;
        }
    }

    public void save() {
        if (this.member != null) {
            this.member.save("loginConfiguration");
        }
    }

    public static String encode(String paramString) {
        byte[] encode = Base64.getEncoder().encode(paramString.getBytes());
        return new String(encode, Charset.forName("UTF-8"));
    }

    public static String decode(String paramString) {
        byte[] decode = Base64.getDecoder().decode(paramString.getBytes());
        return new String(decode, Charset.forName("UTF-8"));
    }

    public Member getMember() {
        return this.member;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isCaptcha() {
        return this.captcha;
    }

    public Map<String, Long> getSessionMap() {
        return this.sessionMap;
    }

    public Map<String, Long> getTimeoutMap() {
        return this.timeoutMap;
    }

    public Map<String, Integer> getAttempsMap() {
        return this.attempsMap;
    }

    public static enum AccountType {
        PREMIUM,
        CRACKED,
        NONE;

    }
}

