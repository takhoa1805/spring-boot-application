import HomePage from "../pages/HomePage";
import GeneratorPage from "../pages/generator/GeneratorPage";
import LogInPage from "../pages/login/LogInPage";
import SignUpPage from "../pages/signup/SignUpPage";
import InvitationPage from "../pages/invitation/InvitationPage";
import ContentPage from "../pages/file_management/ContentPage";
import QuizEditorPage from "../pages/quiz/editor/QuizEditorPage";
import DocumentEditorPage from "../pages/document_editor/DocumentEditorPage";
import DocumentViewerPage from "../pages/document_editor/DocumentViewerPage";
import EnterQuizPage from "../pages/quiz/enter_quiz/EnterQuizPage";
import DoQuizPage from "../pages/quiz/do_quiz/DoQuizPage";
import QuizResultPage from "../pages/quiz/quiz_result/QuizResultPage";
import ReviewQuizPage from "../pages/quiz/review_quiz/ReviewQuizPage";
import ProfilePage from "../pages/profile/ProfilePage";
import QuizHistoryPage from "../pages/quiz/history/QuizHistoryPage";
import ForumPage from "../pages/forum/ForumPage";
import EnterCompetitiveQuizPage from "../pages/competitive_quiz/enter_quiz/EnterCompetitiveQuizPage";
import CompetitiveQuizResultPage from "../pages/competitive_quiz/quiz_result/CompetitiveQuizResultPage";
import ReviewCompetitiveQuizPage from "../pages/competitive_quiz/review_quiz/ReviewCompetitiveQuizPage";
import DoCompetitiveQuizPage from "../pages/competitive_quiz/do_quiz/DoCompetitiveQuizPage";
import AdministrationPage from "../pages/administration/AdministrationPage";
import ProfilePageForAdmin from "../pages/profile/ProfilePageForAdmin";


const routes = [{
        path: "/",
        component: HomePage,
        unauthorized: true,
        authorizedRoute: "/content",
    },
    {
        path: "/profile",
        component: ProfilePage,
        navText: "Profile",
        index: 0,
    },
    {
        path: "/profile/:userId",
        component: ProfilePageForAdmin
    },
    {
        path: "/generator",
        component: GeneratorPage,
        navText: "Resource Generator",
        index: 1,
    },
    {
        path: "/content",
        component: ContentPage,
        navText: "Content",
        index: 2,
    },
    {
        path: "/forum",
        component: ForumPage,
        navText: "Forum",
        index: 3,
    },
    {
        path: "/login",
        component: LogInPage,
        unauthorized: true,
    },
    {
        path: "/signup",
        component: SignUpPage,
        unauthorized: true,
    },
    {
        path: "/verify/:invitationId",
        component: InvitationPage,
        unauthorized: true,
    },
    {
        path: "/documents/:docId/edit",
        component: DocumentEditorPage,
        unauthorized: true,
    },
    {
        path: "/documents/:docId/view",
        component: DocumentViewerPage,
        unauthorized: true,
    },
    {
        path: "/quiz/editor/:resourceId",
        component: QuizEditorPage,
    },
    {
        path: "/quiz/enter/:resourceId",
        component: EnterQuizPage,
    },
    {
        path: "/quiz/tradition/:attemptId",
        component: DoQuizPage,
    },
    {
        path: "/quiz/result/:resourceId",
        component: QuizResultPage,
    },
    {
        path: "/quiz/review/:attemptId",
        component: ReviewQuizPage,
    },
    {
        path: "/quiz/history/:resourceId",
        component: QuizHistoryPage,
    },
    {
        path: "/quiz/competitive/enter/:resourceId",
        component: EnterCompetitiveQuizPage,
    },
    {
        path: "/quiz/competitive",
        component: DoCompetitiveQuizPage,
    },
    {
        path: "/quiz/competitive/result/:sessionMongoId",
        component: CompetitiveQuizResultPage,
    },
    {
        path: "/quiz/competitive/review/:attempId",
        component: ReviewCompetitiveQuizPage,
    },
    {
        path: "/admin",
        component: AdministrationPage,
        navText: "Organization Management",
        index: 4,
    }



];

export default routes;