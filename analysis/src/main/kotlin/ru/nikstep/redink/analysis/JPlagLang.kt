package ru.nikstep.redink.analysis

enum class JPlagLang(val langName: String) {
    JAVA_1_7("java17"),
    JAVA_1_5("java15"),
    JAVA_1_5_DM("java15dm"), JAVA_1_2("java12"), JAVA_1_1("java11"), PYTHON_3("python3"), C_CPP("c/c++"), CSHARP_1_2("c#-1.2"), CHAR(
        "char"
    ),
    TEXT("text"), SCHEME("scheme");

    override fun toString(): String {
        return langName
    }
}