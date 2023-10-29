import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
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
        return desiredTable.attributes;
      })
    );
  }
}
