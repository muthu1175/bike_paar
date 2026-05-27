from django.contrib import admin

# Register your models here.
from .models import Notification, AppSetting

admin.site.register(Notification)
admin.site.register(AppSetting)
