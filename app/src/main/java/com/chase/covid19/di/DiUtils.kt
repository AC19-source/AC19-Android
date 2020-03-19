import com.chase.covid19.application.ApplicationLoader
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

inline fun <reified T : Any> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { GlobalContext.get().koin.get<T>(qualifier, parameters) }


class Injectable {
    val app by inject<ApplicationLoader>()
}