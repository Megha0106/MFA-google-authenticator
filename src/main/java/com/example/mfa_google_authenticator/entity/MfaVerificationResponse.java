package com.example.mfa_google_authenticator.entity;

public record MfaVerificationResponse(
        String userName,
        boolean tokenValid,
        boolean authValid,
        boolean mfaRequired,
        String jwt,
        String message
)
{
    public static class Builder {
        private String userName;
        private boolean tokenValid;
        private boolean authValid;
        private boolean mfaRequired;
        private String jwt;
        private String message;

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }
        public Builder tokenValid(boolean tokenValid) {
            this.tokenValid = tokenValid;
            return this;
        }

        public Builder authValid(boolean authValid) {
            this.authValid = authValid;
            return this;
        }

        public Builder mfaRequired(boolean mfaRequired) {
            this.mfaRequired = mfaRequired;
            return this;
        }

        public Builder jwt(String jwt) {
            this.jwt = jwt;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public MfaVerificationResponse build() {
            return new MfaVerificationResponse(userName, tokenValid, authValid, mfaRequired, jwt, message);
        }
    }
    public static Builder builder() {
        return new Builder();
    }
}
