export interface DatabaseResponse {
    databaseName: string,
    tables: Table[]
}

export interface Table {
    tableName: string,
    fileName: string,
    foreignKeys: ForeignKey[],
    indexes?: Index[],
    uniqueKeys: string[],
    attributes: Attribute[],
    primaryKey: {
        pkAttributes: string[]
    }
}

export interface ForeignKey {
    referencedTable: string,
    foreignKeyAttributes: string[],
    referencedAttributes: string[]
}

export interface Index {
    
        indexName: string,
        isUnique: number,
        indexAttributes: string[]
}

export interface Attribute {
    attributeType: string,
    isNull: number,
    attributeName: string
}