package com.tutorialapi.model;

import java.security.Principal;

public class RapidApiPrincipal implements Principal {
    private final String user;
    private final Subscription subscription;
    private final String proxySecret;

    public RapidApiPrincipal(String user, Subscription subscription, String proxySecret) {
        this.user = user;
        this.subscription = subscription;
        this.proxySecret = proxySecret;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getUser() {
        return user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RapidApiPrincipal that = (RapidApiPrincipal) o;
        return user.equals(that.user) && subscription == that.subscription && proxySecret.equals(that.proxySecret);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + subscription.hashCode();
        result = 31 * result + proxySecret.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RapidApiPrincipal{" +
                "user='" + user + '\'' +
                ", subscription=" + subscription +
                ", proxySecret='" + proxySecret + '\'' +
                '}';
    }
}
