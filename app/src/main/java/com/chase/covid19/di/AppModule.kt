import com.chase.covid19.application.ApplicationLoader
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val AppModule = module {
    single { androidApplication() as ApplicationLoader }
}