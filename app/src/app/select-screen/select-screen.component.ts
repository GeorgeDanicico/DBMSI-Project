import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TablesService } from '../shared/services/tables.service';

@Component({
  selector: 'ado-select-screen',
  templateUrl: './select-screen.component.html',
  styleUrls: ['./select-screen.component.scss'],
})
export class SelectScreenComponent implements OnInit {
  selectForm: FormGroup;
  databaseName: string;
  tableName: string;
  attributes: string[] = [];
  attributesSelected: string[] = [];
  records = [];
  operations: {
    label: string;
    value: string;
  }[] = [
    {
      label: '=',
      value: 'EQUAL',
    },
    {
      label: '<',
      value: 'LESS_THAN',
    },
    {
      label: '>',
      value: 'GREATER_THAN',
    },
    {
      label: 'LIKE',
      value: 'LIKE',
    },
  ];

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private tableService: TablesService) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.databaseName = params.get('databaseName');
      this.tableName = params.get('tableName');
    });

    this.tableService.fetchTableStructure(this.databaseName, this.tableName).subscribe((response) => {
      this.attributes = response.map((attr) => attr.attributeName);
      this.projectionItems.controls[0].get('attributeName').setValue(this.attributes[0]);
      this.conditionsItems.controls[0].get('columnName').setValue(this.attributes[0]);
    });
    this.initForm();
  }

  onSelect() {
    const projection = [];
    const conditions = [];

    for (let i = 0; i < this.projectionItems.length; ++i) {
      const item = this.projectionItems.at(i).value;
      projection.push(item.attributeName);
    }

    this.attributesSelected = [...projection];

    for (let i = 0; i < this.conditionsItems.length; ++i) {
      const item = this.conditionsItems.at(i).value;
      conditions.push({
        ...item,
      });
    }

    const query = {
      projection,
      conditions,
      isDistinct: this.selectForm.get('isDistinct').value,
    };

    this.tableService.fetchTableValuesBySelect(this.databaseName, this.tableName, query).subscribe((response) => {
      this.records = response;
    });
  }

  get projectionItems() {
    return this.selectForm.get('projection') as FormArray;
  }

  get conditionsItems() {
    return this.selectForm.get('conditions') as FormArray;
  }

  deleteProjection(index) {
    this.projectionItems.removeAt(index);
  }

  addProjection() {
    this.projectionItems.push(
      this.fb.group({
        attributeName: ['', Validators.required],
      })
    );
  }

  deleteCondition(index) {
    this.conditionsItems.removeAt(index);
  }

  addCondition() {
    this.conditionsItems.push(
      this.fb.group({
        columnName: ['', Validators.required],
        operation: [this.operations[0], Validators.required],
        value: ['', Validators.required],
      })
    );
  }

  initForm() {
    this.selectForm = this.fb.group({
      isDistinct: false,
      projection: this.fb.array([
        this.fb.group({
          attributeName: [this.attributes[0], Validators.required],
        }),
      ]),
      conditions: this.fb.array([
        this.fb.group({
          columnName: [this.attributes[0], Validators.required],
          operation: [this.operations[0].value, Validators.required],
          value: ['', Validators.required],
        }),
      ]),
    });
  }
}
