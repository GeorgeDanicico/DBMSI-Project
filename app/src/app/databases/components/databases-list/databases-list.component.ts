import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DatabaseResponse } from '../../models/databases-response.model';

@Component({
  selector: 'ado-databases-list',
  templateUrl: './databases-list.component.html',
  styleUrls: ['./databases-list.component.scss'],
})
export class DatabasesListComponent {
  @Input() databases: DatabaseResponse[] = [];
  @Output() databaseSelected = new EventEmitter<DatabaseResponse>();
  @Output() databaseDeleted = new EventEmitter<DatabaseResponse>();

  constructor() {}


  onSelectDatabase(database: DatabaseResponse): void {
    this.databaseSelected.emit(database);
  }

  onDeleteDatabase(database: DatabaseResponse): void {
    this.databaseDeleted.emit(database);
  }
}
