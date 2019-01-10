package ru.nikstep.redink.entity

import javax.persistence.*


@Entity
@Table(name = "p_user")
open class User(
    var name: String = "",
    var email: String = "",
    var pword: String = ""
//    ,
//    @OneToMany(mappedBy = "owner", cascade = arrayOf(CascadeType.ALL)) var repositories: Set<Repository>
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}