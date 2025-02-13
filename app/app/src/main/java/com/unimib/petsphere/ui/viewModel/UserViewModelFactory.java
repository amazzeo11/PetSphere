package com.unimib.petsphere.ui.viewModel;
//Author: Alessia Mazzeo

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.data.repository.UserRepository;
import com.unimib.petsphere.util.ServiceLocator;


public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;

    public UserViewModelFactory(Application app) {
        this.userRepository = ServiceLocator.getInstance().getUserRepository(app);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserViewModel(userRepository);
    }
}