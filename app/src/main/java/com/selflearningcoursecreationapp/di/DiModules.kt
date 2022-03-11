package com.selflearningcoursecreationapp.di

import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.getApiProvider
import com.selflearningcoursecreationapp.data.network.getApiService
import com.selflearningcoursecreationapp.ui.authentication.forgotPass.ForgotPassRepo
import com.selflearningcoursecreationapp.ui.authentication.forgotPass.ForgotPassRepoImpl
import com.selflearningcoursecreationapp.ui.authentication.forgotPass.ForgotPassViewModel
import com.selflearningcoursecreationapp.ui.authentication.login_otp.LoginOTPRepo
import com.selflearningcoursecreationapp.ui.authentication.login_otp.LoginOTPRepoImpl
import com.selflearningcoursecreationapp.ui.authentication.login_otp.LoginOTPViewModel
import com.selflearningcoursecreationapp.ui.authentication.login_signup.OnBoardingRepo
import com.selflearningcoursecreationapp.ui.authentication.login_signup.OnBoardingRepoImpl
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyRepo
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyRepoImpl
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyViewModel
import com.selflearningcoursecreationapp.ui.authentication.resetPassword.ResetPassRepo
import com.selflearningcoursecreationapp.ui.authentication.resetPassword.ResetPassRepoImpl
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.ui.authentication.viewModel.SplashViewModel
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePasswordVM
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FAQViewModel
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileViewModel
import com.selflearningcoursecreationapp.ui.authentication.resetPassword.ResetViewModel
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FAQRepoImpl
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FaqRepo
import com.selflearningcoursecreationapp.ui.create_course.CreateCourseRepo
import com.selflearningcoursecreationapp.ui.create_course.CreateCourseRepoImpl
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.profile.repo.EditProfileRepo
import com.selflearningcoursecreationapp.ui.profile.repo.EditProfileRepoImpl
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.SpeechUtils
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkingModule = module {
    single {
        getApiProvider()
    }
    single {
        getApiService(get())
    }

    single {
        SpeechUtils()
    }
    single {
        ImagePickUtils()
    }
}


fun getAppContext() = SelfLearningApplication.applicationContext()
val viewModelModule = module {
    viewModel {
        ForgotPassViewModel(get())
    }
    viewModel {
        OnBoardingViewModel(get())
    }
    viewModel {
        SplashViewModel()
    }
    viewModel {
        LoginOTPViewModel(get())
    }
    viewModel {
        PreferenceViewModel()
    }
    viewModel {
        ChangePasswordVM()
    }
    viewModel {
        OTPVerifyViewModel(get())
    }
    viewModel {
        ResetViewModel(get())
    }
    viewModel {
        EditProfileViewModel(get())
    }
    viewModel {
        FAQViewModel(get())
    }
    viewModel {
        AddCourseViewModel(get())
    }


}


val repoModule = module {
    single<ForgotPassRepo> {
        ForgotPassRepoImpl(get())
    }
    single<OnBoardingRepo> {
        OnBoardingRepoImpl(get())
    }
    single<LoginOTPRepo> { LoginOTPRepoImpl(get()) }
    single<OTPVerifyRepo> { OTPVerifyRepoImpl(get()) }
    single<ResetPassRepo> { ResetPassRepoImpl(get()) }
    single<EditProfileRepo> { EditProfileRepoImpl(get()) }
    single<FaqRepo> { FAQRepoImpl(get()) }
    single<CreateCourseRepo> { CreateCourseRepoImpl(get()) }
}
