import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Table } from '../../models/databases-response.model';

@Component({
  selector: 'ado-create-table-modal',
  templateUrl: './create-table-modal.component.html',
  styleUrls: ['./create-table-modal.component.scss'],
})
export class CreateTableModalComponent implements OnInit {
  @Input() tables: Table[] = [];
  @Output() tableToSave = new EventEmitter<Table>();

  referencedTablesOptions: string[] = [];
  foreignKeyAttributesOptions: string[] = [];
  referencedAttributesOptions = new Map<number, string[]>();

  attributeTypes = ['VARCHAR(250)', 'INT', 'DATE'];

  attributeForm: FormGroup;

  constructor(private fb: FormBuilder, private activeModalService: NgbActiveModal) {}

  ngOnInit() {
    this.attributeForm = this.fb.group({
      tableName: ['', Validators.required],
      fileName: ['', Validators.required],
      items: this.fb.array([
        this.fb.group({
          isPrimaryKey: false,
          attributeName: ['', Validators.required],
          attributeType: ['', Validators.required],
          allowNulls: false,
          isUnique: false,
        }),
      ]),
      foreignKeys: this.fb.array([]),
    });

    this.foreignKeys.valueChanges.subscribe((value) => {
      for (let i = 0; i < value.length; ++i) {
        const element = value.at(i);
        console.log(element);
        const tables = this.tables.filter((table) => table.tableName === element.referencedTable);

        if (tables.length > 0) {
          const table = tables[0];
          const attributes = table.attributes.map((attribute) => attribute.attributeName);
          this.referencedAttributesOptions.set(i, attributes);

          console.log(i);
          console.log(this.referencedAttributesOptions.get(i))
        }
      }
    });

    this.attributeItems.valueChanges.subscribe((value) => {
      this.foreignKeyAttributesOptions = value.map((v) => v.attributeName);
    });

    this.referencedTablesOptions = this.tables.map((table) => table.tableName);
  }

  get attributeItems() {
    return this.attributeForm.get('items') as FormArray;
  }

  get foreignKeys() {
    return this.attributeForm.get('foreignKeys') as FormArray;
  }

  addAttribute() {
    this.attributeItems.push(
      this.fb.group({
        isPrimaryKey: false,
        attributeName: ['', Validators.required],
        attributeType: ['', Validators.required],
        allowNulls: false,
        isUnique: false,
      })
    );
  }

  deleteAttribute(index) {
    this.attributeItems.removeAt(index);
  }

  deleteForeignKey(index) {
    this.foreignKeys.removeAt(index);
  }

  addForeignKeys(): void {
    this.foreignKeys.push(
      this.fb.group({
        referencedTable: ['', Validators.required],
        foreignKeyAttributes: ['', Validators.required],
        referencedAttributes: ['', Validators.required],
      })
    );
  }

  onClose() {
    this.activeModalService.close();
  }

  onSaveTable() {
    const attributes = [];
    const pkAttributes = [];
    const uniqueKeys = [];
    for (let i = 0; i < this.attributeItems.length; i++) {
      const item = this.attributeItems.at(i);
      if (item.value.isPrimaryKey) {
        pkAttributes.push(item.value.attributeName);
      }
      if (item.value.isUnique) {
        uniqueKeys.push(item.value.attributeName);
      }
      attributes.push({
        attributeName: item.value.attributeName.toString(),
        attributeType: item.value.attributeType.toString(),
        isNull: item.value.allowNulls ? 1 : 0,
      });
    }

    const foreignKeys = [];
    for (let i = 0; i < this.foreignKeys.length; ++i) {
      const key = this.foreignKeys.at(i);
      foreignKeys.push({
        referencedTable: key.value.referencedTable.toString(),
        foreignKeyAttributes: key.value.foreignKeyAttributes.toString().split(' '),
        referencedAttributes: key.value.referencedAttributes.toString().split(' '),
      });
    }

    const tableToSave: Table = {
      fileName: this.attributeForm.value.fileName.toString(),
      tableName: this.attributeForm.value.tableName.toString(),
      attributes: attributes,
      primaryKey: {
        pkAttributes,
      },
      foreignKeys,
      uniqueKeys,
      indexes: [],
    };

    this.tableToSave.emit(tableToSave);
    this.activeModalService.close();
  }
}
