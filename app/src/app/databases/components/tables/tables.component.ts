import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { switchMap } from 'rxjs/operators';
import { ToastService } from 'src/app/shared/components/toasts-container/toasts-service';
import { TablesService } from '../../../shared/services/tables.service';
import { DatabaseResponse, Table } from '../../models/databases-response.model';
import { CreateTableModalComponent } from '../create-table-modal/create-table-modal.component';
@Component({
  selector: 'ado-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss'],
})
export class TablesComponent implements OnChanges {
  @Input() database: DatabaseResponse;
  tables: Table[] = [];
  tableSelected: Table;

  constructor(private modalService: NgbModal, private tableService: TablesService, private toastService: ToastService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.database && this.database) {
      this.tables = this.database.tables;
    }
    this.tableSelected = null;
  }

  onNewTable(): void {
    const modalRef = this.modalService.open(CreateTableModalComponent, { size: 'lg' });
    modalRef.componentInstance.tables = this.tables;
    modalRef.componentInstance.tableToSave
      .pipe(
        switchMap<Table, any>((table) => {
          return this.tableService.saveTable(this.database.databaseName, table);
        }),
        switchMap(() => {
          return this.tableService.fetchTables(this.database.databaseName);
        })
      )
      .subscribe(
        (response) => {
          this.tables = response;
          this.toastService.showSuccess('Table created successfully');
        },
        () => {
          this.toastService.showError('Could not create table');
        }
      );
  }

  onTableToDelete(table: Table) {
    this.tableService
      .deleteTable(this.database.databaseName, table.tableName)
      .pipe(
        switchMap(() => {
          return this.tableService.fetchTables(this.database.databaseName);
        })
      )
      .subscribe(
        (response) => {
          this.tables = response;
          this.tableSelected = null;
          this.toastService.showSuccess('Table deleted successfully');
        },
        () => {
          this.toastService.showError('Could not delete table');
        }
      );
  }

  onTableSelected(table: Table) {
    this.tableSelected = table;
  }
}
