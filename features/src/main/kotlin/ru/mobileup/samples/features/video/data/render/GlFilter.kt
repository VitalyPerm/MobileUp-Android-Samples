package ru.mobileup.samples.features.video.data.render

import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_IMAGE_SIZE_FACTOR
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_INPUT_TEXTURE
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_TEXTURE_COORDINATE

val availableFilters = listOf(
    GlFilter.Default,
    GlFilter.BlackWhite,
    GlFilter.Sepia,
    GlFilter.Noir,
    GlFilter.Posterize,
    GlFilter.Pixellate,
    GlFilter.ColorMap,
    GlFilter.Bump,
    GlFilter.Hole,
    GlFilter.Twirl,
    GlFilter.Vortex,
    GlFilter.Glitch,
    GlFilter.GlitchChroma
)

enum class GlFilter(
    val fragmentShader: String,
) {
    Default(
        fragmentShader = "" +
                "void main()\n" +
                "{\n" +
                "  vec4 color = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);\n" +
                "  gl_FragColor = color;\n" +
                "}",
    ),

    BlackWhite(
        fragmentShader = "" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "void main()\n" +
                "{\n" +
                "  lowp vec4 textureColor = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);\n" +
                "  float luminance = dot(textureColor.rgb, W);\n" +
                "\n" +
                "  gl_FragColor = vec4(vec3(luminance), textureColor.a);\n" +
                "}",
    ),

    Sepia(
        fragmentShader = "" +
                "void main()\n" +
                "{\n" +
                "  vec4 color = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);\n" +
                "  gl_FragColor = vec4(\n" +
                "    clamp(color.x * 0.393 + color.y * 0.769 + color.z * 0.189, 0.0, 1.0),\n" +
                "    clamp(color.x * 0.349 + color.y * 0.686 + color.z * 0.168, 0.0, 1.0),\n" +
                "    clamp(color.x * 0.272 + color.y * 0.534 + color.z * 0.131, 0.0, 1.0),\n" +
                "    1.0\n" +
                "   );\n" +
                "}",
    ),

    Noir(
        fragmentShader = "" +
                "void main()\n" +
                "{\n" +
                "  vec4 textureColor = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);\n" +
                "  float luminance = 0.2125 * textureColor.r + 0.7154 * textureColor.g + 0.0721 * textureColor.b;\n" +
                "  float contrast = 1.5;\n" +
                "  luminance = (luminance - 0.5) * contrast + 0.5;\n" +
                "  float distanceFromCenter = distance($SHADER_TEXTURE_COORDINATE, vec2(0.5, 0.5));\n" +
                "  float vignetteIntensity = 0.5;\n" +
                "  float vignette = 1.0 - vignetteIntensity * distanceFromCenter;\n" +
                "  vignette = clamp(vignette, 0.0, 1.0);\n" +
                "  gl_FragColor = vec4(luminance * vignette, luminance * vignette, luminance * vignette, textureColor.a);\n" +
                "}",
    ),

    Posterize(
        fragmentShader = "" +
                "void main()\n" +
                "{\n" +
                "   float colorLevels = 10.0;\n" +
                "   vec4 textureColor = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);\n" +
                "   gl_FragColor = floor((textureColor * colorLevels) + vec4(0.5)) / colorLevels;\n" +
                "}",

        ),

    Pixellate(
        fragmentShader = "" +
                "void main()\n" +
                "{\n" +
                "  float pixel = 0.01;\n" +
                "  vec2 uv = ${SHADER_TEXTURE_COORDINATE}.xy;\n" +
                "  highp vec2 aspectRatio = vec2(1.0, 1.0);\n" +
                "  float inputAspectRatio = $SHADER_IMAGE_SIZE_FACTOR.x / $SHADER_IMAGE_SIZE_FACTOR.y;\n" +
                "  if (inputAspectRatio <= 1.0) {\n " +
                "      aspectRatio.y = inputAspectRatio;\n " +
                "   } else { \n" +
                "      aspectRatio.x = 1.0 / inputAspectRatio;" +
                "  }\n" +
                "  float dx = pixel * aspectRatio.x;\n" +
                "  float dy = pixel * aspectRatio.y;\n" +
                "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
                "  gl_FragColor = texture2D($SHADER_INPUT_TEXTURE, coord);\n" +
                "}",

        ),

    ColorMap(
        fragmentShader = "" +
                "void main()\n" +
                "{\n" +
                "    vec4 texColor = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);\n" +
                "    float grayscale = dot(texColor.rgb, vec3(0.2, 0.1, 0.2));\n" +
                "    float threshold1 = 0.18;\n" +
                "    float threshold2 = 0.24;\n" +
                "    float threshold3 = 0.28;\n" +
                "    float threshold4 = 1.0;\n" +
                "    vec3 color1 = vec3(0.05, 0.02, 0.996);\n" +
                "    vec3 color2 = vec3(0.0, 1.0, 0.941);\n" +
                "    vec3 color3 = vec3(0.02, 1.0, 0.0);\n" +
                "    vec3 color4 = vec3(1.0, 0.98, 0.0);\n" +
                "    vec3 color5 = vec3(1.0, 0.0, 0.0);\n" +
                "    float coeff = 5.0;\n" +
                "    if (grayscale < threshold1)\n" +
                "        gl_FragColor = vec4(mix(color1, color2, grayscale * coeff), texColor.a);\n" +
                "    else if (grayscale < threshold2)\n" +
                "        gl_FragColor = vec4(mix(color2, color3, (grayscale - threshold1) * coeff), texColor.a);\n" +
                "    else if (grayscale < threshold3)\n" +
                "        gl_FragColor = vec4(mix(color3, color4, (grayscale - threshold2) * coeff), texColor.a);\n" +
                "    else if (grayscale < threshold4)\n" +
                "        gl_FragColor = vec4(mix(color4, color5, (grayscale - threshold3) * coeff), texColor.a);\n" +
                "    else\n" +
                "        gl_FragColor = vec4(color5, texColor.a);\n" +
                "}",
    ),

    Bump(
        fragmentShader =
        "   void main() {\n" +
                "       highp float radius = 0.25;\n" +
                "       highp float scale = 0.5;\n" +
                "       highp vec2 center = vec2(0.5, 0.5);\n" +
                "       highp vec2 aspectRatio = vec2(1.0, 1.0);\n" +
                "       float inputAspectRatio = $SHADER_IMAGE_SIZE_FACTOR.x / $SHADER_IMAGE_SIZE_FACTOR.y;\n" +
                "       if (inputAspectRatio <= 1.0) {\n " +
                "           aspectRatio.y = inputAspectRatio;\n " +
                "       } else { \n" +
                "           aspectRatio.x = 1.0 / inputAspectRatio;" +
                "       }\n" +
                "       highp vec2 textureCoordinateToUse = $SHADER_TEXTURE_COORDINATE;\n" +
                "       textureCoordinateToUse /= aspectRatio;\n" +
                "       center /= aspectRatio;\n" +
                "       highp float dist = distance(center, textureCoordinateToUse);\n" +
                "       textureCoordinateToUse -= center;\n" +
                "       if (dist < radius) {\n" +
                "           highp float percent = 1.0 - ((radius - dist) / radius) * scale;\n" +
                "           percent = percent * percent;\n" +
                "           textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
                "       }\n" +
                "       textureCoordinateToUse += center;\n" +
                "       textureCoordinateToUse *= aspectRatio;\n" +
                "       gl_FragColor = texture2D($SHADER_INPUT_TEXTURE, textureCoordinateToUse);\n" +
                "}",

        ),

    Hole(
        fragmentShader =
        "   void main() {\n" +
                "       highp float radius = 0.18;\n" +
                "       highp float holeRadius = 0.08;\n" +
                "       highp vec2 center = vec2(0.5, 0.5);\n" +
                "       highp vec2 aspectRatio = vec2(1.0, 1.0);\n" +
                "       float inputAspectRatio = $SHADER_IMAGE_SIZE_FACTOR.x / $SHADER_IMAGE_SIZE_FACTOR.y;\n" +
                "       if (inputAspectRatio <= 1.0) {\n " +
                "           aspectRatio.y = inputAspectRatio;\n " +
                "       } else { \n" +
                "           aspectRatio.x = 1.0 / inputAspectRatio;" +
                "       }\n" +
                "       highp vec2 textureCoordinateToUse = $SHADER_TEXTURE_COORDINATE;\n" +
                "       textureCoordinateToUse /= aspectRatio;\n" +
                "       center /= aspectRatio;\n" +
                "       highp float dist = distance(center, textureCoordinateToUse);\n" +
                "       textureCoordinateToUse -= center;\n" +
                "       if (dist < holeRadius) {\n" +
                "           gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                "           return;\n" +
                "       } " +
                "       else if (dist < radius) {\n" +
                "           float t = smoothstep(holeRadius, radius, dist);\n" +
                "           float smoothDist = holeRadius + (dist - holeRadius) * t;\n" +
                "           float distortion = ((smoothDist - holeRadius) / (radius - holeRadius));\n" +
                "           textureCoordinateToUse *= distortion;\n" +
                "       }\n" +
                "       textureCoordinateToUse += center;\n" +
                "       textureCoordinateToUse *= aspectRatio;\n" +
                "       gl_FragColor = texture2D($SHADER_INPUT_TEXTURE, textureCoordinateToUse);\n" +
                "}",

        ),

    Twirl(
        fragmentShader =
        "   void main() {\n" +
                "       highp float radius = 0.40;\n" +
                "       highp float angle = -200.0;\n" +
                "       highp vec2 center = vec2(0.5, 0.5);\n" +
                "       highp vec2 aspectRatio = vec2(1.0, 1.0);\n" +
                "       float inputAspectRatio = $SHADER_IMAGE_SIZE_FACTOR.x / $SHADER_IMAGE_SIZE_FACTOR.y;\n" +
                "       if (inputAspectRatio <= 1.0) {\n " +
                "           aspectRatio.y = inputAspectRatio;\n " +
                "       } else { \n" +
                "           aspectRatio.x = 1.0 / inputAspectRatio;" +
                "       }\n" +
                "       highp vec2 textureCoordinateToUse = $SHADER_TEXTURE_COORDINATE;\n" +
                "       textureCoordinateToUse /= aspectRatio;\n" +
                "       center /= aspectRatio;\n" +
                "       textureCoordinateToUse -= center;\n" +
                "       vec2 st = textureCoordinateToUse.st;\n" +
                "       float r = length(st);\n" +
                "       float beta = atan(st.y, st.x) + radians(angle) * (radius - r) / radius;\n" +
                "       if (r <= radius) {\n" +
                "           st.s = r * vec2(cos(beta)).s;\n" +
                "           st.t = r * vec2(sin(beta)).t;\n" +
                "       }\n" +
                "       st += center;\n" +
                "       st *= aspectRatio;\n" +
                "       vec3 irgb = texture2D($SHADER_INPUT_TEXTURE, st).rgb;\n" +
                "       gl_FragColor = vec4(irgb, 1.0);\n" +
                "}",
    ),

    Vortex(
        fragmentShader =
        "   void main() {\n" +
                "       highp float radius = 0.15;\n" +
                "       highp float angle = -0.8;\n" +
                "       highp vec2 center = vec2(0.5, 0.5);\n" +
                "       highp vec2 aspectRatio = vec2(1.0, 1.0);\n" +
                "       float inputAspectRatio = $SHADER_IMAGE_SIZE_FACTOR.x / $SHADER_IMAGE_SIZE_FACTOR.y;\n" +
                "       if (inputAspectRatio <= 1.0) {\n " +
                "           aspectRatio.y = inputAspectRatio;\n " +
                "       } else { \n" +
                "           aspectRatio.x = 1.0 / inputAspectRatio;" +
                "       }\n" +
                "       highp vec2 textureCoordinateToUse = $SHADER_TEXTURE_COORDINATE;\n" +
                "       textureCoordinateToUse /= aspectRatio;\n" +
                "       center /= aspectRatio;\n" +
                "       highp float dist = distance(center, textureCoordinateToUse);\n" +
                "       textureCoordinateToUse -= center;\n" +
                "       if (dist < radius) {\n" +
                "           highp float percent = (radius - dist) / radius;\n" +
                "           highp float theta = percent * percent * angle * 8.0;\n" +
                "           highp float s = sin(theta);\n" +
                "           highp float c = cos(theta);\n" +
                "           textureCoordinateToUse = vec2(" +
                "                   dot(textureCoordinateToUse, vec2(c, -s)), " +
                "                   dot(textureCoordinateToUse, vec2(s, c))" +
                "           );\n" +
                "       }\n" +
                "       textureCoordinateToUse += center;\n" +
                "       textureCoordinateToUse *= aspectRatio;\n" +
                "       gl_FragColor = texture2D($SHADER_INPUT_TEXTURE, textureCoordinateToUse);\n" +
                "}",
    ),

    Glitch(
        fragmentShader =
        "void main() {\n" +
                "    mat3 uKernel = mat3( 1., -1.0, 1., \n" +
                "                        0., 1., 0., \n" +
                "                        -1., 1.0, -1. );\n" +
                "    vec4 sum = vec4(0.0);\n" +
                "    float multiplier = 2.5;\n" +
                "    vec2 step = vec2(" +
                "           (1.0 / $SHADER_IMAGE_SIZE_FACTOR.x) * multiplier, " +
                "           (1.0 / $SHADER_IMAGE_SIZE_FACTOR.y) * multiplier" +
                "    );\n" +

                "    float xStep = $SHADER_TEXTURE_COORDINATE.x + step.x;\n" +
                "    float xNegativeStep = $SHADER_TEXTURE_COORDINATE.x - step.x;\n" +
                "    float yStep = $SHADER_TEXTURE_COORDINATE.y + step.y;\n" +
                "    float yNegativeStep = $SHADER_TEXTURE_COORDINATE.y - step.y;\n" +

                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2(xNegativeStep, yNegativeStep)) * uKernel[0][0];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2($SHADER_TEXTURE_COORDINATE.x, yNegativeStep)) * uKernel[0][1];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2(xStep, yNegativeStep)) * uKernel[0][2];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2(xNegativeStep, $SHADER_TEXTURE_COORDINATE.y)) * uKernel[1][0];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2($SHADER_TEXTURE_COORDINATE.x, $SHADER_TEXTURE_COORDINATE.y)) * uKernel[1][1];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2(xStep, $SHADER_TEXTURE_COORDINATE.y)) * uKernel[1][2];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2(xNegativeStep, yNegativeStep)) * uKernel[2][0];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2($SHADER_TEXTURE_COORDINATE.x, yStep)) * uKernel[2][1];\n" +
                "    sum += texture2D($SHADER_INPUT_TEXTURE, " +
                "               vec2(xStep, yStep)) * uKernel[2][2];\n" +
                "    sum.a = 1.0;\n" +
                "    gl_FragColor = sum;\n" +
                "}",
    ),

    GlitchChroma(
        fragmentShader =
        "   const int num_iter = 12;\n" +
                "const float reci_num_iter_f = 1.0 / float(num_iter);\n" +
                "\n" +
                "vec2 barrelDistortion(vec2 coord, float amt) {\n" +
                "    vec2 cc = coord - 0.5;\n" +
                "    float dist = dot(cc, cc);\n" +
                "    return coord + cc * dist * amt;\n" +
                "}\n" +
                "\n" +
                "float sat( float t )\n" +
                "{\n" +
                "    return clamp(t, 0.0, 1.0);\n" +
                "}\n" +
                "\n" +
                "float linterp(float t) {\n" +
                "    return sat(1.0 - abs(2.0*t - 1.0));\n" +
                "}\n" +
                "\n" +
                "float remap(float t, float a, float b) {\n" +
                "    return sat((t - a) / (b - a));\n" +
                "}\n" +
                "\n" +
                "vec3 spectrum_offset(float t) {\n" +
                "    vec3 ret;\n" +
                "    float lo = step(t, 0.5);\n" +
                "    float hi = 1.0 - lo;\n" +
                "    float w = linterp(remap(t, 1.0 / 6.0, 5.0 / 6.0));\n" +
                "    ret = vec3(lo, 1.0, hi) * vec3(1.0 - w, w, 1.0 - w);\n" +
                "\n" +
                "    return pow(ret, vec3(1.0 / 2.2));\n" +
                "}\n" +
                "\n" +
                "void main()\n" +
                "{   \n" +
                "    float barrelPower = 2.2;\n" +
                "    vec2 sketchSize = vec2(1./$SHADER_IMAGE_SIZE_FACTOR.x, 1./$SHADER_IMAGE_SIZE_FACTOR.y);\n" +
                "    vec2 uv = $SHADER_TEXTURE_COORDINATE.xy;\n" +
                "\n" +
                "    vec3 sumcol = vec3(0.0);\n" +
                "    vec3 sumw = vec3(0.0);\n" +
                "    for (int i = 0; i < num_iter; ++i)\n" +
                "    {\n" +
                "        float t = float(i) * reci_num_iter_f;\n" +
                "        vec3 w = spectrum_offset(t);\n" +
                "        sumw += w;\n" +
                "        sumcol += w * texture2D($SHADER_INPUT_TEXTURE, barrelDistortion(uv, barrelPower * t)).rgb;\n" +
                "    }\n" +
                "    gl_FragColor = vec4(sumcol.rgb / sumw, 1.0);\n" +
                "}",
    );

    fun getFullFragmentShader(externalTexture: Boolean): String {
        val extensionDeclaration = if (externalTexture) {
            "#extension GL_OES_EGL_image_external : require\n"
        } else {
            ""
        }

        val samplerDeclaration = if (externalTexture) {
            "uniform samplerExternalOES $SHADER_INPUT_TEXTURE;\n"
        } else {
            "uniform sampler2D $SHADER_INPUT_TEXTURE;\n"
        }

        return extensionDeclaration +
                "precision mediump float;\n" +
                samplerDeclaration +
                "uniform vec2 $SHADER_IMAGE_SIZE_FACTOR;\n" +
                "varying vec2 $SHADER_TEXTURE_COORDINATE;\n" +
                fragmentShader
    }
}