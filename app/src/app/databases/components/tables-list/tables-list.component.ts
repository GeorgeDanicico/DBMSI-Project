import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Table } from '../../models/databases-response.model';

@Component({
  selector: 'ado-tables-list',
  templateUrl: './tables-list.component.html',
  styleUrls: ['./tables-list.component.scss'],
})
export class TablesListComponent {
  @Input() tables: Table[] = [];
  @Output() tableDeleted = new EventEmitter<Table>();
  @Output() tableSelected = new EventEmitter<Table>();

  constructor() {}

  onSelectTable(table: Table) {
    this.tableSelected.emit(table);
  }


  onDeleteTable(table) {
    this.tableDeleted.emit(table);
  }
}
