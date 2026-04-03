from django.urls import path
from .views import *
from .views import upload_profile_image

from django.urls import path
from .views import SearchBikeAPIView
from .views import FavouriteBikeAPIView
from .views import BikeReviewAPIView
from .views import AppReviewAPIView
from .views import BikeCompareAPIView
from .views import AllBikesAPIView
from .views import Below100ccBikesAPIView
from .views import Bikes100to150ccAPIView
from .views import Bikes150to200ccAPIView
from .views import Bikes200to350ccAPIView
from .views import Bikes350to500ccAPIView
from .views import Bikes500to750ccAPIView
from .views import Bikes750to1000ccAPIView
from .views import BikesAbove1000ccAPIView
from .views import BikesByBudgetAPIView
from .views import Bikes30kto80kAPIView, Bikes80kto150kAPIView, Bikes150kto300kAPIView, Bikes300kto500kAPIView, Bikes5Lto10LAPIView, Bikes10Lto30LAPIView, BikesAbove30LAPIView
from .views import SportsBikesAPIView
from .views import ScooterBikesAPIView
from .views import CruiserBikesAPIView
from .views import CommuterBikesAPIView
from .views import StreetBikesAPIView
from .views import SuperBikesAPIView
from .views import ScramblerBikesAPIView
from .views import AdventureBikesAPIView
from .views import TourerBikesAPIView
from .views import FeedbackAPIView
from .views import BikesByBrandAPIView
from .views import BikeDetailsAPIView
from .views import BikeFullSpecificationsAPIView
from .views import PopularBikesAPIView
from .views import RecentLaunchesAPIView
urlpatterns = [
    path('signup/', signup, name='signup'), 
     path('login/', login, name='login'),
    path('forgot-password/', ForgotPasswordView.as_view(), name='forgot-password'),
     path('upload-profile-image/', upload_profile_image),
       path('ai-suggest/', AiSuggestAPIView.as_view()),
       path("search/", SearchBikeAPIView.as_view(), name="search-bikes"),
       path("favourites/", FavouriteBikeAPIView.as_view()),
       path("reviews/", BikeReviewAPIView.as_view()),
       path("app-reviews/", AppReviewAPIView.as_view(), name="app-reviews"),
       path("compare/", BikeCompareAPIView.as_view(), name="bike-compare"),
       path("bikes/", AllBikesAPIView.as_view()),
       path("bikes/below-100cc/", Below100ccBikesAPIView.as_view()),
       path("bikes/100-150cc/", Bikes100to150ccAPIView.as_view()),
       path("bikes/150-200cc/", Bikes150to200ccAPIView.as_view()),
       path("bikes/200-350cc/", Bikes200to350ccAPIView.as_view()),
        path("bikes/350-500cc/", Bikes350to500ccAPIView.as_view()),
        path("bikes/500-750cc/", Bikes500to750ccAPIView.as_view()),
        path("bikes/750-1000cc/", Bikes750to1000ccAPIView.as_view()),
        path("bikes/above-1000cc/", BikesAbove1000ccAPIView.as_view()),
        path("bikes/30k-80k/", Bikes30kto80kAPIView.as_view()),
        path("bikes/80k-150k/", Bikes80kto150kAPIView.as_view()),
        path("bikes/150k-300k/", Bikes150kto300kAPIView.as_view()),
        path("bikes/300k-500k/", Bikes300kto500kAPIView.as_view()),
        path("bikes/5l-10l/", Bikes5Lto10LAPIView.as_view()),
        path("bikes/10l-30l/", Bikes10Lto30LAPIView.as_view()),
        path("bikes/above-30l/", BikesAbove30LAPIView.as_view()),
        path("bikes/by-budget/", BikesByBudgetAPIView.as_view()),
        path("bikes/sports/", SportsBikesAPIView.as_view()),
        path("bikes/scooters/", ScooterBikesAPIView.as_view()),
        path("bikes/cruisers/", CruiserBikesAPIView.as_view()),
        path("bikes/commuters/", CommuterBikesAPIView.as_view()),
        path("bikes/street/", StreetBikesAPIView.as_view()),
        path("bikes/super/", SuperBikesAPIView.as_view()),
        path("bikes/scramblers/", ScramblerBikesAPIView.as_view()),
        path("bikes/adventure/", AdventureBikesAPIView.as_view()),
        path("bikes/tourer/", TourerBikesAPIView.as_view()),
        path("feedback/", FeedbackAPIView.as_view()),
        path("bikes/by-brand/", BikesByBrandAPIView.as_view()),
        path("bikes/details/", BikeDetailsAPIView.as_view()),
        path("bikes/full-specs/", BikeFullSpecificationsAPIView.as_view()),
        path('send-email-otp/', SendEmailOTPView.as_view(), name='send-email-otp'),
        path('verify-email-otp/', VerifyEmailOTPView.as_view(), name='verify-email-otp'),
        path('bikes/popular/', PopularBikesAPIView.as_view(), name='popular-bikes'),
        path('bikes/recent/', RecentLaunchesAPIView.as_view(), name='recent-bikes'),
]




