export interface Query {
    projection: string[],
    conditions: Condition[],
    isDistinct: boolean,
    joinConditions: JoinCondition[]
}

export interface Condition {
    columnName: string,
    operation: string,
    value: string
}

export interface JoinCondition {
    columnName: string,
    tableName: string
}