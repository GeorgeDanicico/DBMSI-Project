import { DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { Attribute } from 'src/app/databases/models/databases-response.model';

@Component({
  selector: 'ado-insert-value-modal',
  templateUrl: './insert-value-modal.component.html',
  styleUrls: ['./insert-value-modal.component.scss'],
})
export class InsertValueModalComponent implements OnInit {
  @Input() attributes: Attribute[] = [];
  @Output() rowsToSave = new EventEmitter<any[]>();

  tableForm: FormGroup;

  constructor(private activeModalService: NgbActiveModal, private fb: FormBuilder) {}

  ngOnInit(): void {
    const dynamicFields: { [key: string]: any } = {};

    this.attributes.forEach((attribute) => {
      dynamicFields[attribute.attributeName] = ['', Validators.required];
    });

    this.tableForm = this.fb.group({
      items: this.fb.array([this.fb.group(dynamicFields)]),
    });
  }

  get attributeItems() {
    return this.tableForm.get('items') as FormArray;
  }

  onClose() {
    this.activeModalService.close();
  }

  onSave() {
    const rows = [];

    for (let i = 0; i < this.attributeItems.length; ++i) {
      const item = this.attributeItems.at(i);
      const row = {};
      this.attributes.forEach((attribute) => {
        if (attribute.attributeType === 'DATE') {
          row[attribute.attributeName] = this.convertNgbDateToStringDate(item.value[attribute.attributeName]);
        }
        else {
          row[attribute.attributeName] = item.value[attribute.attributeName];
        }
      });
      rows.push(row);
    }
    this.rowsToSave.emit(rows);
    this.onClose();
  }

  deleteRow(index: number) {
    this.attributeItems.removeAt(index);
  }

  addRow() {
    const dynamicFields: { [key: string]: any } = {};

    this.attributes.forEach((attribute) => {
      dynamicFields[attribute.attributeName] = ['', Validators.required];
    });

    this.attributeItems.push(this.fb.group(dynamicFields));
  }

  convertNgbDateToStringDate(ngbDate: NgbDate): string {
    if (ngbDate) {
      const newDate = new Date(ngbDate.year, ngbDate.month - 1, ngbDate.day);
      return this.convertDateToStringDate(newDate);
    }
    return '';
  }

  convertDateToStringDate(date: Date): string {
    const d = new DatePipe('en-US').transform(date, 'yyyy-MM-dd');

    if (d) {
      return d;
    }

    return '';
  }
}
