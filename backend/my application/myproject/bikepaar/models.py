from django.db import models

# Create your models here.
from django.db import models
from django.contrib.auth.models import User

class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    image = models.ImageField(upload_to='profile_images/', null=True, blank=True)
    is_email_verified = models.BooleanField(default=False)

    def __str__(self):
        return f"{self.user.username} - {'Verified' if self.is_email_verified else 'Not Verified'}"

# ... (Previous Bike model code)

class EmailOTP(models.Model):
    email = models.EmailField(unique=True)
    otp = models.CharField(max_length=6)
    is_verified = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.email} - {self.otp} - {'Verified' if self.is_verified else 'Pending'}"

from django.db import models

class Bike(models.Model):
    name = models.CharField(max_length=100)
    price = models.IntegerField()
    category = models.CharField(max_length=50)   # commuter, sport, cruiser
    usage = models.CharField(max_length=50)      # daily, adventure
    comfort = models.CharField(max_length=50)    # high, medium, sporty
    experience = models.CharField(max_length=50) # beginner, intermediate
    mileage = models.IntegerField()
    image_url = models.URLField()

    def __str__(self):
        return self.name

from django.db import models
from django.contrib.auth.models import User

class FavouriteBike(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    bike_id = models.CharField(max_length=100)
    model = models.CharField(max_length=100)
    brand = models.CharField(max_length=100)
    price = models.CharField(max_length=50)
    image = models.URLField(blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ("user", "bike_id")

    def __str__(self):
        return f"{self.user.username} - {self.model}"

from django.db import models
from django.contrib.auth.models import User

class BikeReview(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    bike_id = models.CharField(max_length=150)   # Changed to CharField to support Model Names from Excel
    rating = models.IntegerField()
    review = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.username} - {self.rating}"
    
    from django.db import models
from django.contrib.auth.models import User

class AppReview(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    rating = models.IntegerField()
    review = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.username} - {self.rating}★"

from django.db import models

class Feedback(models.Model):
    user_id = models.CharField(max_length=50)
    feedback = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.user_id

import uuid

class EmailVerification(models.Model):
    email = models.EmailField(unique=True)
    token = models.UUIDField(default=uuid.uuid4, editable=False)
    is_verified = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.email} - {'Verified' if self.is_verified else 'Pending'}"
