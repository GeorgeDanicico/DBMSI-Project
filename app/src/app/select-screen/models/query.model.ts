export interface Query {
    projection: string[],
    conditions: Condition[],
    isDistinct: boolean
}

export interface Condition {
    columnName: string,
    operation: string,
    value: string
}