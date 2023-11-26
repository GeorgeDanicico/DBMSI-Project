import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { switchMap } from 'rxjs/operators';
import { ToastService } from 'src/app/shared/components/toasts-container/toasts-service';
import { TablesService } from '../../../shared/services/tables.service';
import { Index, Table } from '../../models/databases-response.model';
import { CreateIndexModalComponent } from '../create-index-modal/create-index-modal.component';

@Component({
  selector: 'ado-table-detailed',
  templateUrl: './table-detailed.component.html',
  styleUrls: ['./table-detailed.component.scss'],
})
export class TableDetailedComponent {
  @Input() databaseName: string;
  @Input() table: Table;

  constructor(
    private router: Router,
    private modalService: NgbModal,
    private tableService: TablesService,
    private toastService: ToastService
  ) {}

  onCreateIndex() {
    const modalRef = this.modalService.open(CreateIndexModalComponent);
    modalRef.componentInstance.indexToSave
      .pipe(
        switchMap((index: Index) => {
          return this.tableService.saveIndex(this.databaseName, this.table.tableName, index);
        })
      )
      .subscribe(
        () => {
          this.toastService.showSuccess('Table index created successfully');
        },
        () => {
          this.toastService.showError('Could not create index for table');
        }
      );
  }

  onViewAllRows() {
    this.router.navigate([`/databases/${this.databaseName}/tables/${this.table.tableName}`]);
  }

  onSelectValues() {
    this.router.navigate([`/databases/${this.databaseName}/tables/${this.table.tableName}/select`]);
  }
}
