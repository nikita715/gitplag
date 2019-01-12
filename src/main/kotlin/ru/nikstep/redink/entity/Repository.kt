package ru.nikstep.redink.entity

import javax.persistence.*

@Entity
@Table(name = "repository")
class Repository(
    @ManyToOne
    @JoinColumn(name = "owner")
    var owner: User,
    var name: String,
    @Column(name = "pattern")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "repository_pattern", joinColumns = arrayOf(JoinColumn(name = "repository")))
    var filePatterns: MutableList<String> = mutableListOf(),
    var githubId: Long
) {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


}