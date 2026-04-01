export interface MockUser {
    name: string;
    role: string;
    serviceId: string;
    rankOrRelation: string;
    unitOrLink: string;
}

const officials: MockUser[] = [
    { name: "Gen. B. Rawat", role: "Officer", serviceId: "OFF-001", rankOrRelation: "General", unitOrLink: "Army HQ" },
    { name: "Col. S. Sharma", role: "Officer", serviceId: "OFF-002", rankOrRelation: "Colonel", unitOrLink: "1st Battalion" },
    { name: "Major R. Singh", role: "Officer", serviceId: "OFF-003", rankOrRelation: "Major", unitOrLink: "Northern Command" },
    { name: "Lt. K. Verma", role: "Officer", serviceId: "OFF-004", rankOrRelation: "Lieutenant", unitOrLink: "Signal Corps" },
    { name: "Capt. A. Khan", role: "Officer", serviceId: "OFF-005", rankOrRelation: "Captain", unitOrLink: "Intelligence Unit" },
    { name: "Cmdr. V. Iyer", role: "Officer", serviceId: "OFF-006", rankOrRelation: "Commander", unitOrLink: "Naval Base" },
    { name: "Brig. N. Gupta", role: "Officer", serviceId: "OFF-007", rankOrRelation: "Brigadier", unitOrLink: "Eastern Command" },
    { name: "Lt. Col. P. Reddy", role: "Officer", serviceId: "OFF-008", rankOrRelation: "Lt Colonel", unitOrLink: "Special Forces" },
    { name: "Major J. Das", role: "Officer", serviceId: "OFF-009", rankOrRelation: "Major", unitOrLink: "Artillery Reg" },
    { name: "Capt. M. Roy", role: "Officer", serviceId: "OFF-010", rankOrRelation: "Captain", unitOrLink: "Border Security" }
];

const soldiers: MockUser[] = [
    { name: "Hav. A. Patil", role: "Soldier", serviceId: "SOL-001", rankOrRelation: "Havildar", unitOrLink: "1st Inf Div" },
    { name: "Naik J. Singh", role: "Soldier", serviceId: "SOL-002", rankOrRelation: "Naik", unitOrLink: "2nd Inf Div" },
    { name: "Sep. R. Prasad", role: "Soldier", serviceId: "SOL-003", rankOrRelation: "Sepoy", unitOrLink: "3rd Inf Div" },
    { name: "L/Naik S. Kumar", role: "Soldier", serviceId: "SOL-004", rankOrRelation: "Lance Naik", unitOrLink: "4th Inf Div" },
    { name: "Hav. D. Joshi", role: "Soldier", serviceId: "SOL-005", rankOrRelation: "Havildar", unitOrLink: "5th Inf Div" },
    { name: "Sep. M. Ali", role: "Soldier", serviceId: "SOL-006", rankOrRelation: "Sepoy", unitOrLink: "6th Inf Div" },
    { name: "Naik P. Meena", role: "Soldier", serviceId: "SOL-007", rankOrRelation: "Naik", unitOrLink: "7th Inf Div" },
    { name: "Sep. G. Soren", role: "Soldier", serviceId: "SOL-008", rankOrRelation: "Sepoy", unitOrLink: "8th Inf Div" },
    { name: "L/Naik H. Gill", role: "Soldier", serviceId: "SOL-009", rankOrRelation: "Lance Naik", unitOrLink: "9th Inf Div" },
    { name: "Sep. K. Negi", role: "Soldier", serviceId: "SOL-010", rankOrRelation: "Sepoy", unitOrLink: "10th Inf Div" },
    { name: "Hav. L. Thapa", role: "Soldier", serviceId: "SOL-011", rankOrRelation: "Havildar", unitOrLink: "11th Inf Div" },
    { name: "Naik B. Gope", role: "Soldier", serviceId: "SOL-012", rankOrRelation: "Naik", unitOrLink: "12th Inf Div" },
    { name: "Sep. V. Murmu", role: "Soldier", serviceId: "SOL-013", rankOrRelation: "Sepoy", unitOrLink: "13th Inf Div" },
    { name: "L/Naik R. Rao", role: "Soldier", serviceId: "SOL-014", rankOrRelation: "Lance Naik", unitOrLink: "14th Inf Div" },
    { name: "Sep. T. Mishra", role: "Soldier", serviceId: "SOL-015", rankOrRelation: "Sepoy", unitOrLink: "15th Inf Div" },
    { name: "Hav. K. Yadav", role: "Soldier", serviceId: "SOL-016", rankOrRelation: "Havildar", unitOrLink: "16th Inf Div" },
    { name: "Naik S. Kurian", role: "Soldier", serviceId: "SOL-017", rankOrRelation: "Naik", unitOrLink: "17th Inf Div" },
    { name: "Sep. A. Pawar", role: "Soldier", serviceId: "SOL-018", rankOrRelation: "Sepoy", unitOrLink: "18th Inf Div" },
    { name: "L/Naik D. Bisht", role: "Soldier", serviceId: "SOL-019", rankOrRelation: "Lance Naik", unitOrLink: "19th Inf Div" },
    { name: "Sep. M. Dhillon", role: "Soldier", serviceId: "SOL-020", rankOrRelation: "Sepoy", unitOrLink: "20th Inf Div" }
];

const families: MockUser[] = [...officials, ...soldiers].map(user => ({
    name: `${user.name.split(" ").pop()} Family`,
    role: "Family",
    serviceId: `FAM-${user.serviceId}`,
    rankOrRelation: "Dependent",
    unitOrLink: user.serviceId
}));

export const MockDataset = {
    officials,
    soldiers,
    families,
    allUsers: [...officials, ...soldiers, ...families]
};
