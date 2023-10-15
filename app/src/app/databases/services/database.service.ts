import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DatabaseResponse } from '../models/databases-response.model';

@Injectable({ providedIn: 'root' })
export class DatabaseService {
  constructor(private http: HttpClient) {}

  getDatabases(): Observable<DatabaseResponse[]> {
    return this.http
      .get<{ databases: DatabaseResponse[] }>('http://localhost:8080/api/databases')
      .pipe(map((response) => response.databases));
  }

  saveDatabase(name: string): Observable<void>{
    return this.http.post<void>('http://localhost:8080/api/databases', { name });
  }

  deleteDatabase(name: string): Observable<void>{
    return this.http.delete<void>(`http://localhost:8080/api/databases/${name}`);
  }
}
