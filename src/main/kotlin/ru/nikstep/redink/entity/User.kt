package ru.nikstep.redink.entity

import javax.persistence.*


@Entity
@Table(name = "p_user")
open class User(
    var name: String,
    var githubId: Long,
    var installationId: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}