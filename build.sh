#!/bin/bash

echo "🔍 Checking project root..."

# التأكد أن ملف settings.gradle أو build.gradle موجود
if [[ ! -f "settings.gradle" && ! -f "build.gradle" ]]; then
  echo "❌ لا يمكن إيجاد settings.gradle أو build.gradle"
  echo "⚠️ يجب تشغيل السكربت داخل جذر مشروع Android"
  exit 1
fi

echo "✔ Found project root."

echo "🔧 Generating Gradle Wrapper..."
gradle wrapper --gradle-version 8.0.2

if [[ ! -f "./gradlew" ]]; then
  echo "❌ فشل إنشاء gradlew!"
  exit 1
fi

chmod +x ./gradlew

echo "🚀 Building APK (Release)..."
./gradlew assembleRelease

APK_PATH=$(find . -name "*.apk" | grep "release")

if [[ -z "$APK_PATH" ]]; then
  echo "❌ فشل العثور على APK!"
  exit 1
fi

echo "🎉 تم إنشاء APK بنجاح!"
echo "📦 موقع الملف:"
echo "$APK_PATH"
