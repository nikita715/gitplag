package ru.nikstep.redink.util

/**
 * Name of a programming language
 */
enum class Language {
    C,
    CPP,
    JAVA,
    ML,
    PASCAL,
    ADA,
    LISP,
    SCHEME,
    HASKELL,
    FORTRAN,
    TEXT,
    VHDL,
    PERL,
    MATLAB,
    PYTHON,
    MIPS_ASSEMBLY,
    PROLOG,
    SPICE,
    VISUAL_BASIC,
    CSHARP,
    MODULA2,
    A8086_ASSEMBLY,
    JAVASCRIPT,
    PLSQL,
    VERILOG,
    TCL;

    /**
     * To moss name of the language
     */
    fun ofMoss() =
        when (this) {
            C -> "c"
            CPP -> "cc"
            JAVA -> "java"
            ML -> "ml"
            PASCAL -> "pascal"
            ADA -> "ada"
            LISP -> "lisp"
            SCHEME -> "scheme"
            HASKELL -> "haskell"
            FORTRAN -> "fortran"
            TEXT -> "ascii"
            VHDL -> "vhdl"
            PERL -> "perl"
            MATLAB -> "matlab"
            PYTHON -> "python"
            MIPS_ASSEMBLY -> "mips"
            PROLOG -> "prolog"
            SPICE -> "spice"
            VISUAL_BASIC -> "vb"
            CSHARP -> "csharp"
            MODULA2 -> "modula2"
            A8086_ASSEMBLY -> "a8086"
            JAVASCRIPT -> "javascript"
            PLSQL -> "plsql"
            VERILOG -> "verilog"
            TCL -> "tcl"
        }

    /**
     * To jplag name of the language
     */
    fun ofJPlag() =
        when (this) {
            C, CPP -> "c/c++"
            JAVA -> "java17"
            SCHEME -> "scheme"
            PYTHON -> "python3"
            CSHARP -> "c#-1.2"
            TEXT -> "text"
            else -> "text"
        }

}