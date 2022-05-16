package com.selflearningcoursecreationapp.di

import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.getApiProvider
import com.selflearningcoursecreationapp.data.network.getApiService
import com.selflearningcoursecreationapp.ui.authentication.add_email.AddEmailRepo
import com.selflearningcoursecreationapp.ui.authentication.add_email.AddEmailRepoImpl
import com.selflearningcoursecreationapp.ui.authentication.add_email.AddEmailVM
import com.selflearningcoursecreationapp.ui.authentication.add_password.AddPassRepo
import com.selflearningcoursecreationapp.ui.authentication.add_password.AddPassRepoImp
import com.selflearningcoursecreationapp.ui.authentication.add_password.AddPassViewModel
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
import com.selflearningcoursecreationapp.ui.authentication.resetPassword.ResetViewModel
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.ui.authentication.viewModel.SplashViewModel
import com.selflearningcoursecreationapp.ui.bottom_home.HomeRepo
import com.selflearningcoursecreationapp.ui.bottom_home.HomeRepoImp
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePassRepo
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePassRepoImp
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePasswordVM
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FAQRepoImpl
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FAQViewModel
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FaqRepo
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepo
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepoImpl
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture.DocRepo
import com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture.DocRepoImp
import com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture.DocViewModel
import com.selflearningcoursecreationapp.ui.create_course.docs_text.TextRepo
import com.selflearningcoursecreationapp.ui.create_course.docs_text.TextRepoImp
import com.selflearningcoursecreationapp.ui.create_course.docs_text.TextViewModel
import com.selflearningcoursecreationapp.ui.create_course.quiz.AddQuizRepo
import com.selflearningcoursecreationapp.ui.create_course.quiz.AddQuizRepoImpl
import com.selflearningcoursecreationapp.ui.create_course.quiz.AddQuizVM
import com.selflearningcoursecreationapp.ui.create_course.quiz.QuizSettingVM
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceRepo
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceRepoImpl
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceVM
import com.selflearningcoursecreationapp.ui.practice_accent.PracticeAccentVM
import com.selflearningcoursecreationapp.ui.preferences.PreferenceRepo
import com.selflearningcoursecreationapp.ui.preferences.PreferenceRepoImpl
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileRepo
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileRepoImpl
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileViewModel
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailRepo
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailRepoImp
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailViewModel
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbRepo
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbRepoImp
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbViewModel
import com.selflearningcoursecreationapp.ui.splash.SplashRepo
import com.selflearningcoursecreationapp.ui.splash.SplashRepoImp
import com.selflearningcoursecreationapp.ui.splash.SplashVM
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
        PreferenceViewModel(get())
    }
    viewModel {
        ChangePasswordVM(get())
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
    viewModel {
        ProfileThumbViewModel(get())
    }
    viewModel { ProfileDetailViewModel(get()) }

    viewModel {
        PracticeAccentVM()
    }

    viewModel {
        SplashVM(get())
    }
    viewModel { HomeVM(get()) }
    viewModel { SingleChoiceVM(get()) }

    viewModel { AddPassViewModel(get()) }
    viewModel { AddEmailVM(get()) }
    viewModel { DocViewModel(get()) }
    viewModel { AddQuizVM(get()) }
    viewModel { QuizSettingVM(get()) }
    viewModel { TextViewModel(get()) }

}


val repoModule = module {
    single<ForgotPassRepo> {
        ForgotPassRepoImpl(get())
    }
    single<OnBoardingRepo> { OnBoardingRepoImpl(get()) }
    single<LoginOTPRepo> { LoginOTPRepoImpl(get()) }
    single<OTPVerifyRepo> { OTPVerifyRepoImpl(get()) }
    single<ResetPassRepo> { ResetPassRepoImpl(get()) }
    single<EditProfileRepo> { EditProfileRepoImpl(get()) }
    single<FaqRepo> { FAQRepoImpl(get()) }
    single<AddCourseRepo> { AddCourseRepoImpl(get()) }
    single<ProfileThumbRepo> { ProfileThumbRepoImp(get()) }
    single<ProfileDetailRepo> { ProfileDetailRepoImp(get()) }
    single<ChangePassRepo> { ChangePassRepoImp(get()) }
    single<PreferenceRepo> { PreferenceRepoImpl(get()) }
    single<SplashRepo> { SplashRepoImp(get()) }
    single<HomeRepo> { HomeRepoImp(get()) }
    single<SingleChoiceRepo> { SingleChoiceRepoImpl(get()) }
    single<AddPassRepo> { AddPassRepoImp(get()) }
    single<AddEmailRepo> { AddEmailRepoImpl(get()) }
    single<AddQuizRepo> { AddQuizRepoImpl(get()) }
    single<DocRepo> { DocRepoImp(get()) }
    single<TextRepo> { TextRepoImp(get()) }
}
