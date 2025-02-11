package com.unimib.petsphere.data.model;
//Author: Alessia Mazzeo

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        return !(this instanceof Error);
    }

    public static final class PetSuccess extends Result {
        private final PetResponseModel petResponseModel;
        public PetSuccess(PetResponseModel petResponseModel) {
            this.petResponseModel = petResponseModel;
        }
        public PetResponseModel getData() {
            return petResponseModel;
        }
    }



    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

    public static final class UserSuccess extends Result {
        private final User user;
        public UserSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }

}
