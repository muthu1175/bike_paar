from rest_framework import serializers
from django.contrib.auth.models import User

class SignupSerializer(serializers.Serializer):
    username = serializers.CharField()
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True, min_length=6)
    confirm_password = serializers.CharField(write_only=True)

    def validate(self, data):
        if data['password'] != data['confirm_password']:
            raise serializers.ValidationError(
                {"confirm_password": "Passwords do not match"}
            )

        if User.objects.filter(username=data['username']).exists():
            raise serializers.ValidationError(
                {"username": "Username already exists"}
            )

        if User.objects.filter(email=data['email']).exists():
            raise serializers.ValidationError(
                {"email": "Email already exists"}
            )

        return data

    def create(self, validated_data):
        validated_data.pop('confirm_password')  # 🔥 IMPORTANT

        user = User.objects.create_user(
            username=validated_data['username'],
            email=validated_data['email'],
            password=validated_data['password']
        )
        return user



from rest_framework import serializers
from django.contrib.auth import authenticate
from django.contrib.auth.models import User

class LoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True)

    def validate(self, data):
        try:
            user = User.objects.get(email=data['email'])
        except User.DoesNotExist:
            raise serializers.ValidationError("Invalid email or password")

        user = authenticate(username=user.username, password=data['password'])
        if not user:
            raise serializers.ValidationError("Invalid email or password")

        data['user'] = user
        return data
    
    from rest_framework import serializers
from django.contrib.auth.models import User

class ForgotPasswordSerializer(serializers.Serializer):
    username = serializers.CharField()
    email = serializers.EmailField()
    new_password = serializers.CharField(write_only=True)
    confirm_new_password = serializers.CharField(write_only=True)

    def validate(self, data):
        if data['new_password'] != data['confirm_new_password']:
            raise serializers.ValidationError("Passwords do not match")

        try:
            # Signup saves email as username, and display name as first_name
            # So we look up by email
            user = User.objects.get(email=data['email'])
            
            # Optional: verify the "User Name" provided matches the display name (first_name)
            # We strip and lower case for better UX
            if user.first_name.strip().lower() != data['username'].strip().lower():
                 raise serializers.ValidationError("Username does not match the email provided")

        except User.DoesNotExist:
            raise serializers.ValidationError("User not found")

        data['user'] = user
        return data

    def save(self):
        user = self.validated_data['user']
        user.set_password(self.validated_data['new_password'])
        user.save()
        return user

from rest_framework import serializers
from .models import Bike

class BikeSerializer(serializers.ModelSerializer):
    match_score = serializers.IntegerField()

    class Meta:
        model = Bike
        fields = [
            "name",
            "price",
            "category",
            "usage",
            "comfort",
            "experience",
            "mileage",
            "image_url",
            "match_score"
        ]

from rest_framework import serializers
from .models import FavouriteBike

class FavouriteBikeSerializer(serializers.ModelSerializer):
    class Meta:
        model = FavouriteBike
        fields = "__all__"


from rest_framework import serializers
from .models import BikeReview

class BikeReviewSerializer(serializers.ModelSerializer):
    user = serializers.StringRelatedField(read_only=True)

    class Meta:
        model = BikeReview
        fields = [
            "id",
            "user",
            "bike_id",
            "rating",
            "review",
            "created_at"
        ]


from rest_framework import serializers
from .models import AppReview

class AppReviewSerializer(serializers.ModelSerializer):
    user_name = serializers.CharField(source="user.username", read_only=True)
    user_id = serializers.IntegerField(source="user.id", read_only=True)

    class Meta:
        model = AppReview
        fields = [
            "id",
            "user_id",
            "user_name",
            "rating",
            "review",
            "created_at"
        ]

from rest_framework import serializers
from .models import Bike

class BikeCompareSerializer(serializers.ModelSerializer):
    class Meta:
        model = Bike
        fields = [
            "id",
            "name",
            "price",
            "displacement",
            "max_power",
            "max_torque",
            "mileage",
            "fuel_type",
            "transmission",
            "kerb_weight",
            "fuel_tank",
            "brand"
        ]

from rest_framework import serializers
from .models import Feedback

class FeedbackSerializer(serializers.ModelSerializer):
    class Meta:
        model = Feedback
        fields = "__all__"

from .models import Notification

class NotificationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Notification
        fields = '__all__'
