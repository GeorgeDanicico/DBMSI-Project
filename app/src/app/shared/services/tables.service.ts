import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Query } from 'src/app/select-screen/models/query.model';
import { Attribute, Index, Table } from '../../databases/models/databases-response.model';

@Injectable({ providedIn: 'root' })
export class TablesService {
  constructor(private http: HttpClient) {}

  fetchTables(databaseName: string): Observable<Table[]> {
    return this.http
      .get<{ tables: Table[] }>(`http://localhost:8080/api/databases/${databaseName}/tables`)
      .pipe(map((response) => response.tables));
  }

  saveTable(databaseName: string, table: Table): Observable<void> {
    return this.http.post<void>(`http://localhost:8080/api/databases/${databaseName}/tables`, { ...table });
  }

  fetchTableValuesBySelect(databaseName: string, tableName: string, query: Query): Observable<[]> {
    return this.http
      .post<{ records: [] }>(`http://localhost:8080/api/databases/${databaseName}/tables/${tableName}/select`, { ...query })
      .pipe(map((resp) => resp.records));
  }

  deleteTable(databaseName: string, tableName: string): Observable<void> {
    return this.http.delete<void>(`http://localhost:8080/api/databases/${databaseName}/tables/${tableName}`);
  }

  saveIndex(databaseName: string, tableName: string, index: Index): Observable<void> {
    return this.http.post<void>(`http://localhost:8080/api/databases/${databaseName}/tables/${tableName}/index`, { ...index });
  }

  fetchTableStructure(databaseName: string, tableName: string): Observable<Attribute[]> {
    return this.http.get<{ tables: Table[] }>(`http://localhost:8080/api/databases/${databaseName}/tables`).pipe(
      map((response) => {
        const tables: Table[] = response.tables;
        const desiredTable = tables.find((table) => table.tableName === tableName);
        desiredTable.attributes.map((attr) => (attr.isPrimaryKey = desiredTable.primaryKey.pkAttributes.includes(attr.attributeName)));
        return desiredTable.attributes;
      })
    );
  }

  saveRow(databaseName: string, tableName: string, row: any) {
    return this.http.post(`http://localhost:8080/api/databases/${databaseName}/tables/${tableName}/insert`, row);
  }

  fetchTableValues(databaseName: string, tableName: string): Observable<any[]> {
    return this.http
      .get<{ records: any[] }>(`http://localhost:8080/api/databases/${databaseName}/tables/${tableName}/rows`)
      .pipe(map((response) => response.records));
  }

  deleteTableRow(databaseName: string, tableName: string, rowId: string) {
    return this.http.delete(`http://localhost:8080/api/databases/${databaseName}/tables/${tableName}/delete/${rowId}`);
  }
}
