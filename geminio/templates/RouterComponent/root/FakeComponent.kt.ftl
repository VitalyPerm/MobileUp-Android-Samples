package ${packageName}.${packagePath}

import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow

class Fake${componentName} : ${componentName} {

    override val childStack = createFakeChildStackStateFlow(${componentName}.Child.Default)

}