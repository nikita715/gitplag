package ru.nikstep.redink.entity

import javax.persistence.*

@Entity
@Table(name = "repository")
class Repository {
    @ManyToOne
    @JoinColumn(name = "owner")
    lateinit var owner: User
    lateinit var link: String
    lateinit var name: String
    @Column(name = "pattern")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "repository_pattern", joinColumns = arrayOf(JoinColumn(name = "repository")))
    var filePatterns: MutableList<String> = mutableListOf()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


}