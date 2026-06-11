package com.sfedu.degree_android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient

@HiltAndroidApp
class App : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        // Надёжно: ключ + init в Application
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
    }

    override fun newImageLoader(): ImageLoader {
        val cacheInterceptor = Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            response.newBuilder()
                .header("Cache-Control", "max-age=31536000, public")
                .removeHeader("Pragma")
                .build()
        }

        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(cacheInterceptor)
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
