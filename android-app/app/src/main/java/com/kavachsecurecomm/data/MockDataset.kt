package com.kavachsecurecomm.data

data class MockUser(
    val name: String,
    val role: String,
    val serviceId: String,
    val rankOrRelation: String,
    val unitOrLink: String
)

object MockDataset {
    val officials = listOf(
        MockUser("Gen. B. Rawat", "Officer", "OFF-001", "General", "Army HQ"),
        MockUser("Col. S. Sharma", "Officer", "OFF-002", "Colonel", "1st Battalion"),
        MockUser("Major R. Singh", "Officer", "OFF-003", "Major", "Northern Command"),
        MockUser("Lt. K. Verma", "Officer", "OFF-004", "Lieutenant", "Signal Corps"),
        MockUser("Capt. A. Khan", "Officer", "OFF-005", "Captain", "Intelligence Unit"),
        MockUser("Cmdr. V. Iyer", "Officer", "OFF-006", "Commander", "Naval Base"),
        MockUser("Brig. N. Gupta", "Officer", "OFF-007", "Brigadier", "Eastern Command"),
        MockUser("Lt. Col. P. Reddy", "Officer", "OFF-008", "Lt Colonel", "Special Forces"),
        MockUser("Major J. Das", "Officer", "OFF-009", "Major", "Artillery Reg"),
        MockUser("Capt. M. Roy", "Officer", "OFF-010", "Captain", "Border Security")
    )

    val soldiers = listOf(
        MockUser("Hav. A. Patil", "Soldier", "SOL-001", "Havildar", "1st Inf Div"),
        MockUser("Naik J. Singh", "Soldier", "SOL-002", "Naik", "2nd Inf Div"),
        MockUser("Sep. R. Prasad", "Soldier", "SOL-003", "Sepoy", "3rd Inf Div"),
        MockUser("L/Naik S. Kumar", "Soldier", "SOL-004", "Lance Naik", "4th Inf Div"),
        MockUser("Hav. D. Joshi", "Soldier", "SOL-005", "Havildar", "5th Inf Div"),
        MockUser("Sep. M. Ali", "Soldier", "SOL-006", "Sepoy", "6th Inf Div"),
        MockUser("Naik P. Meena", "Soldier", "SOL-007", "Naik", "7th Inf Div"),
        MockUser("Sep. G. Soren", "Soldier", "SOL-008", "Sepoy", "8th Inf Div"),
        MockUser("L/Naik H. Gill", "Soldier", "SOL-009", "Lance Naik", "9th Inf Div"),
        MockUser("Sep. K. Negi", "Soldier", "SOL-010", "Sepoy", "10th Inf Div"),
        MockUser("Hav. L. Thapa", "Soldier", "SOL-011", "Havildar", "11th Inf Div"),
        MockUser("Naik B. Gope", "Soldier", "SOL-012", "Naik", "12th Inf Div"),
        MockUser("Sep. V. Murmu", "Soldier", "SOL-013", "Sepoy", "13th Inf Div"),
        MockUser("L/Naik R. Rao", "Soldier", "SOL-014", "Lance Naik", "14th Inf Div"),
        MockUser("Sep. T. Mishra", "Soldier", "SOL-015", "Sepoy", "15th Inf Div"),
        MockUser("Hav. K. Yadav", "Soldier", "SOL-016", "Havildar", "16th Inf Div"),
        MockUser("Naik S. Kurian", "Soldier", "SOL-017", "Naik", "17th Inf Div"),
        MockUser("Sep. A. Pawar", "Soldier", "SOL-018", "Sepoy", "18th Inf Div"),
        MockUser("L/Naik D. Bisht", "Soldier", "SOL-019", "Lance Naik", "19th Inf Div"),
        MockUser("Sep. M. Dhillon", "Soldier", "SOL-020", "Sepoy", "20th Inf Div")
    )

    val families = (officials + soldiers).map { user ->
        MockUser(
            name = "${user.name.split(" ").last()} Family",
            role = "Family",
            serviceId = "FAM-${user.serviceId}",
            rankOrRelation = "Dependent",
            unitOrLink = user.serviceId
        )
    }

    val allUsers = officials + soldiers + families
}
