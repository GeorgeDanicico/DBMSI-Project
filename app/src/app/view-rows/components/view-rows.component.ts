import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Attribute } from 'src/app/databases/models/databases-response.model';
import { ToastService } from 'src/app/shared/components/toasts-container/toasts-service';
import { TablesService } from 'src/app/shared/services/tables.service';
import { InsertValueModalComponent } from './modals/insert-value-modal/insert-value-modal.component';

@Component({
  selector: 'ado-view-rows',
  templateUrl: './view-rows.component.html',
  styleUrls: ['./view-rows.component.scss'],
})
export class ViewRowsComponent implements OnInit {
  databaseName: string;
  tableName: string;
  attributes: Attribute[] = [];
  valueRows: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private tableService: TablesService,
    private modalService: NgbModal,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.databaseName = params.get('databaseName');
      this.tableName = params.get('tableName');
    });

    this.tableService.fetchTableStructure(this.databaseName, this.tableName).subscribe((response) => {
      this.attributes = response;
    });

    this.tableService.fetchTableValues(this.databaseName, this.tableName).subscribe((response) => {
      this.valueRows = response;
    });
  }

  onInsertValue() {
    const modalRef = this.modalService.open(InsertValueModalComponent, { size: 'lg' });
    modalRef.componentInstance.attributes = this.attributes;
    modalRef.componentInstance.rowsToSave.subscribe((rows) => {
      rows.forEach((row) => {
        this.tableService.saveRow(this.databaseName, this.tableName, row).subscribe(
          (response: { message: string }) => {
            this.toastService.showSuccess(response.message);
            this.tableService.fetchTableValues(this.databaseName, this.tableName).subscribe((response) => {
              this.valueRows = response;
            });
          },
          (error) => {
            this.toastService.showError(error.error);
          }
        );
      });
    });
  }

  onDelete(row) {
    const attrPrimaryKey = this.attributes.find((attr) => attr.isPrimaryKey);
    this.tableService.deleteTableRow(this.databaseName, this.tableName, row[attrPrimaryKey.attributeName]).subscribe(
      (response: {message: string}) => {
        this.toastService.showSuccess(response.message);
        this.tableService.fetchTableValues(this.databaseName, this.tableName).subscribe(response => {
          this.valueRows = response;
        })
      },
      (error) => {
        this.toastService.showError(error.error);
      }
    );
  }
}
