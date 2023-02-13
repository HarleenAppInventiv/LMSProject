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
import com.selflearningcoursecreationapp.ui.bottom_course.MyCourseVM
import com.selflearningcoursecreationapp.ui.bottom_course.MyCoursesRepo
import com.selflearningcoursecreationapp.ui.bottom_course.MyCoursesRepoImp
import com.selflearningcoursecreationapp.ui.bottom_home.HomeRepo
import com.selflearningcoursecreationapp.ui.bottom_home.HomeRepoImp
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.bottom_home.categories.HomeCategoriesVM
import com.selflearningcoursecreationapp.ui.bottom_home.categories.HomeCategoryRepo
import com.selflearningcoursecreationapp.ui.bottom_home.categories.HomeCategoryRepoImpl
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.AllCoursesRepo
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.AllCoursesRepoImpl
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.AllCoursesVM
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.HomeFilterRepo
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.HomeFilterRepoImpl
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.HomeFilterVM
import com.selflearningcoursecreationapp.ui.bottom_more.MoreFragmentRepo
import com.selflearningcoursecreationapp.ui.bottom_more.MoreFragmentRepoImp
import com.selflearningcoursecreationapp.ui.bottom_more.MoreFragmentVM
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsFragmentRepo
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsFragmentRepoImp
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsVM
import com.selflearningcoursecreationapp.ui.bottom_more.payments.details.PaymentDetailVM
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePassRepo
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePassRepoImp
import com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword.ChangePasswordVM
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FAQRepoImpl
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FAQViewModel
import com.selflearningcoursecreationapp.ui.bottom_more.settings.faq.FaqRepo
import com.selflearningcoursecreationapp.ui.content_creator.course_detail.RevenueFragmentRepo
import com.selflearningcoursecreationapp.ui.content_creator.course_detail.RevenueFragmentRepoImp
import com.selflearningcoursecreationapp.ui.content_creator.course_detail.RevenueFragmentVM
import com.selflearningcoursecreationapp.ui.course_details.AuthorDetailsVM
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailRepo
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailRepoImpl
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.course_details.authorDetail.AuthorDetailsFragmentRepo
import com.selflearningcoursecreationapp.ui.course_details.authorDetail.AuthorDetailsRepoImpl
import com.selflearningcoursecreationapp.ui.course_details.quiz.TakeQuizRepo
import com.selflearningcoursecreationapp.ui.course_details.quiz.TakeQuizRepoImpl
import com.selflearningcoursecreationapp.ui.course_details.quiz.TakeQuizVM
import com.selflearningcoursecreationapp.ui.course_details.video.ContentDetailViewModel
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepo
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepoImpl
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.CoAuthorRepo
import com.selflearningcoursecreationapp.ui.create_course.co_author.CoAuthorRepoImpl
import com.selflearningcoursecreationapp.ui.create_course.co_author.CoAuthorViewModel
import com.selflearningcoursecreationapp.ui.create_course.quiz.*
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepoImpl
import com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson.AudioLessonViewModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture.DocViewModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text.TextViewModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.editVideo.AudioEditVM
import com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture.VideoLessonViewModel
import com.selflearningcoursecreationapp.ui.dashboard.LearnerDashboardRepo
import com.selflearningcoursecreationapp.ui.dashboard.LearnerDashboardRepoImp
import com.selflearningcoursecreationapp.ui.dashboard.LearnerDashboardVM
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.CreatorDashFilterVM
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.CreatorDashboardRepo
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.CreatorDashboardRepoImp
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.CreatorDashboardVM
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceRepo
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceRepoImpl
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceVM
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockRepo
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockRepoImp
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockVM
import com.selflearningcoursecreationapp.ui.home.HomeActivityRepo
import com.selflearningcoursecreationapp.ui.home.HomeActivityRepoImp
import com.selflearningcoursecreationapp.ui.home.HomeActivityViewModel
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.ModCourseDetailRepo
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.ModCourseDetailRepoImp
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.ModCourseDetailVM
import com.selflearningcoursecreationapp.ui.moderator.dialog.filter.ModHomeFilterVM
import com.selflearningcoursecreationapp.ui.moderator.editCourse.ModEditRepo
import com.selflearningcoursecreationapp.ui.moderator.editCourse.ModEditRepoImp
import com.selflearningcoursecreationapp.ui.moderator.editCourse.ModEditVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard.ModDashboardRepo
import com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard.ModDashboardRepoImp
import com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard.ModDashboardVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeRepo
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeRepoImp
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.accepted.ModApprovedVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.pending.ModPendingVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.rejected.ModRejectedVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.request.ModRequestVM
import com.selflearningcoursecreationapp.ui.moderator.myCategories.MyCategoriesRepo
import com.selflearningcoursecreationapp.ui.moderator.myCategories.MyCategoriesRepoImpl
import com.selflearningcoursecreationapp.ui.moderator.myCategories.MyCategoriesVM
import com.selflearningcoursecreationapp.ui.moderator.qualification.ModCertificateRepo
import com.selflearningcoursecreationapp.ui.moderator.qualification.ModCertificateRepoImp
import com.selflearningcoursecreationapp.ui.moderator.qualification.ModCertificateVM
import com.selflearningcoursecreationapp.ui.my_bank.BankAccountRepo
import com.selflearningcoursecreationapp.ui.my_bank.BankAccountRepoImpl
import com.selflearningcoursecreationapp.ui.my_bank.BankAccountVM
import com.selflearningcoursecreationapp.ui.notification.NotificationRepo
import com.selflearningcoursecreationapp.ui.notification.NotificationRepoImp
import com.selflearningcoursecreationapp.ui.notification.NotificationVM
import com.selflearningcoursecreationapp.ui.offlineCourse.AddOfflineCourseVM
import com.selflearningcoursecreationapp.ui.offlineCourse.OfflineCourseDetailVM
import com.selflearningcoursecreationapp.ui.offlineCourse.OfflineCourseVM
import com.selflearningcoursecreationapp.ui.payment.CheckoutRepo
import com.selflearningcoursecreationapp.ui.payment.CheckoutRepoImpl
import com.selflearningcoursecreationapp.ui.payment.CheckoutVM
import com.selflearningcoursecreationapp.ui.practice_accent.PracticeAccentVM
import com.selflearningcoursecreationapp.ui.preferences.PreferenceRepo
import com.selflearningcoursecreationapp.ui.preferences.PreferenceRepoImpl
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.ui.profile.bookmark.PagingDataSource
import com.selflearningcoursecreationapp.ui.profile.bookmark.WishListRepository
import com.selflearningcoursecreationapp.ui.profile.bookmark.WishListRepositoryImpl
import com.selflearningcoursecreationapp.ui.profile.bookmark.WishListViewModel
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileRepo
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileRepoImpl
import com.selflearningcoursecreationapp.ui.profile.edit_profile.EditProfileViewModel
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailRepo
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailRepoImp
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailViewModel
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbRepo
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbRepoImp
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbViewModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestTrackerRepo
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestTrackerRepoImp
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.ui.profile.reward.RewardsRepository
import com.selflearningcoursecreationapp.ui.profile.reward.RewardsRepositoryImpl
import com.selflearningcoursecreationapp.ui.profile.reward.viewModel.RewardViewModel
import com.selflearningcoursecreationapp.ui.search.SearchFragmentRepo
import com.selflearningcoursecreationapp.ui.search.SearchFragmentRepoImp
import com.selflearningcoursecreationapp.ui.search.SearchFragmentVM
import com.selflearningcoursecreationapp.ui.splash.SplashRepo
import com.selflearningcoursecreationapp.ui.splash.SplashRepoImp
import com.selflearningcoursecreationapp.ui.splash.SplashVM
import com.selflearningcoursecreationapp.utils.ExceptionHandler
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
    single {
        ExceptionHandler()
    }
    single { getDatabase() }
}

fun getDatabase() = SelfLearningApplication.mDatabase
fun getAppContext() = SelfLearningApplication.applicationContext()
val viewModelModule = module {
    viewModel {
        ForgotPassViewModel(get())
    }
    viewModel {
        OnBoardingViewModel(get())
    }

    viewModel {
        LoginOTPViewModel(get())
    }

    viewModel {
        WishListViewModel(get())
    }
    viewModel {
        RewardViewModel(get())
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
    viewModel { ProfileThumbViewModel(get()) }
    viewModel { ProfileDetailViewModel(get()) }

    viewModel {
        PracticeAccentVM()
    }

    viewModel {
        SplashVM(get())
    }
    viewModel { HomeVM(get(), get()) }
    viewModel { SingleChoiceVM(get()) }

    viewModel { AddPassViewModel(get()) }
    viewModel { AddEmailVM(get()) }
    viewModel { DocViewModel(get()) }
    viewModel { AddQuizVM(get()) }
    viewModel { QuizSettingVM(get()) }
    viewModel { TextViewModel(get()) }
    viewModel { AudioLessonViewModel(get()) }
    viewModel { VideoLessonViewModel(get()) }
    viewModel { CoAuthorViewModel(get()) }
    viewModel { HomeActivityViewModel(get()) }
    viewModel { AddQuizAnsVM(get()) }
    viewModel { HomeCategoriesVM(get()) }
    viewModel { HomeFilterVM(get()) }
    viewModel { AllCoursesVM(get()) }
    viewModel { CourseDetailVM(get()) }
    viewModel { ContentDetailViewModel(get()) }
    viewModel { TakeQuizVM(get()) }
    viewModel { MyCourseVM(get()) }
    viewModel { PaymentDetailVM(get()) }
    viewModel { ModCourseDetailVM(get()) }
    viewModel { UnlockVM(get()) }
    viewModel { RequestrackerVM(get()) }
    viewModel { ModHomeVM(get()) }
    viewModel { MoreFragmentVM(get()) }
    viewModel { ModRequestVM(get()) }
    viewModel { ModPendingVM(get()) }
    viewModel { ModApprovedVM(get()) }
    viewModel { ModRejectedVM(get()) }
    viewModel { ModDashboardVM(get()) }
    viewModel { ModHomeFilterVM(get()) }
    viewModel { LearnerDashboardVM(get()) }
    viewModel { SearchFragmentVM(get()) }
    viewModel { ModCertificateVM(get()) }
    viewModel { ModEditVM(get()) }
    viewModel { RevenueFragmentVM(get()) }
    viewModel { NotificationVM(get()) }
    viewModel { AuthorDetailsVM(get()) }
    viewModel { PaymentsVM(get()) }
    viewModel { AddOfflineCourseVM(get()) }
    viewModel { OfflineCourseVM(get()) }
    viewModel { OfflineCourseDetailVM(get()) }
    viewModel { CreatorDashboardVM(get()) }
    viewModel { CreatorDashFilterVM(get()) }
    viewModel { CheckoutVM(get()) }
    viewModel { BankAccountVM(get()) }
    viewModel { AudioEditVM() }
    viewModel { MyCategoriesVM(get()) }


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
    single<UploadContentRepo> { UploadContentRepoImpl(get()) }
    single { PagingDataSource(get()) }
    single<WishListRepository> { WishListRepositoryImpl(get()) }
    single<RewardsRepository> { RewardsRepositoryImpl(get()) }

    single<HomeActivityRepo> {
        HomeActivityRepoImp(get())
    }
    single<CoAuthorRepo> { CoAuthorRepoImpl(get()) }
    single<HomeCategoryRepo> { HomeCategoryRepoImpl(get()) }
    single<HomeFilterRepo> { HomeFilterRepoImpl(get()) }
    single<AllCoursesRepo> { AllCoursesRepoImpl(get()) }
    single<CourseDetailRepo> { CourseDetailRepoImpl(get()) }
    single<TakeQuizRepo> { TakeQuizRepoImpl(get()) }
    single<MyCoursesRepo> { MyCoursesRepoImp(get()) }
    single<UnlockRepo> { UnlockRepoImp(get()) }
    single<RequestTrackerRepo> { RequestTrackerRepoImp(get()) }
    single<ModHomeRepo> { ModHomeRepoImp(get()) }
    single<MoreFragmentRepo> { MoreFragmentRepoImp(get()) }

    single<ModDashboardRepo> { ModDashboardRepoImp(get()) }
    single<RevenueFragmentRepo> { RevenueFragmentRepoImp(get()) }
    single<LearnerDashboardRepo> { LearnerDashboardRepoImp(get()) }
    single<ModCourseDetailRepo> { ModCourseDetailRepoImp(get()) }
    single<ModCertificateRepo> { ModCertificateRepoImp(get()) }
    single<ModEditRepo> { ModEditRepoImp(get()) }
    single<NotificationRepo> { NotificationRepoImp(get()) }
    single<AuthorDetailsFragmentRepo> { AuthorDetailsRepoImpl(get()) }
    single<PaymentsFragmentRepo> { PaymentsFragmentRepoImp(get()) }
    single<CreatorDashboardRepo> { CreatorDashboardRepoImp(get()) }
    single<SearchFragmentRepo> { SearchFragmentRepoImp(get()) }
    single<CheckoutRepo> { CheckoutRepoImpl(get()) }
    single<BankAccountRepo> { BankAccountRepoImpl(get()) }
    single<MyCategoriesRepo> { MyCategoriesRepoImpl(get()) }

}
