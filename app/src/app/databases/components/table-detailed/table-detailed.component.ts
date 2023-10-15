import { Component, Input } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { switchMap } from 'rxjs/operators';
import { ToastService } from 'src/app/shared/components/toasts-container/toasts-service';
import { Index, Table } from '../../models/databases-response.model';
import { TablesService } from '../../services/tables.service';
import { CreateIndexModalComponent } from '../create-index-modal/create-index-modal.component';

@Component({
  selector: 'ado-table-detailed',
  templateUrl: './table-detailed.component.html',
  styleUrls: ['./table-detailed.component.scss'],
})
export class TableDetailedComponent {
  @Input() databaseName: string;
  @Input() table: Table;

  constructor(private modalService: NgbModal, private tableService: TablesService, private toastService: ToastService) {}

  onCreateIndex() {
    const modalRef = this.modalService.open(CreateIndexModalComponent);
    modalRef.componentInstance.indexToSave
      .pipe(
        switchMap((index: Index) => {
          return this.tableService.saveIndex(this.databaseName, this.table.tableName, index);
        })
      )
      .subscribe(() => {
        this.toastService.showSuccess('Table index created successfully');
      }, () => {
        this.toastService.showError('Could not create index for table');
      });
  }
}
