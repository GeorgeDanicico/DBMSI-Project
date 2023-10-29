import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Attribute } from 'src/app/databases/models/databases-response.model';
import { TablesService } from 'src/app/shared/services/tables.service';
import { InsertValueModalComponent } from './modals/insert-value-modal/insert-value-modal.component';

@Component({
  selector: 'ado-view-rows',
  templateUrl: './view-rows.component.html',
  styleUrls: ['./view-rows.component.scss']
})
export class ViewRowsComponent implements OnInit {
  databaseName: string;
  tableName: string;
  attributes: Attribute[] = [];

  constructor(private route: ActivatedRoute, private tableService: TablesService, private modalService: NgbModal) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.databaseName = params.get('databaseName');
      this.tableName = params.get('tableName');
    });

    this.tableService.fetchTableStructure(this.databaseName, this.tableName).subscribe(response => {
      this.attributes = response;
    })

  }

  onInsertValue(){
    const modalRef = this.modalService.open(InsertValueModalComponent, { size: 'lg' });
    modalRef.componentInstance.attributes = this.attributes;
  }


}
