import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Attribute } from 'src/app/databases/models/databases-response.model';

@Component({
  selector: 'ado-insert-value-modal',
  templateUrl: './insert-value-modal.component.html',
  styleUrls: ['./insert-value-modal.component.scss'],
})
export class InsertValueModalComponent implements OnInit {
  @Input() attributes: Attribute[] = [];

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
}
