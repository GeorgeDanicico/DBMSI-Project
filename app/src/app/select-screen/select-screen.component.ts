import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TablesService } from '../shared/services/tables.service';
import { Table } from '../databases/models/databases-response.model';

@Component({
  selector: 'ado-select-screen',
  templateUrl: './select-screen.component.html',
  styleUrls: ['./select-screen.component.scss'],
})
export class SelectScreenComponent implements OnInit {
  selectForm: FormGroup;
  databaseName: string;
  tableName: string;
  attributesProjection: string[] = [];
  tables: Table[];
  tablesName: string[];
  attributesJoin: string[][] = [];
  attributesCondition: string[][] = [];
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
      this.attributesProjection = response.map((attr) => attr.attributeName);
      this.projectionItems.controls[0].get('attributeName').setValue(this.attributesProjection[0]);
    });

    this.tableService.fetchTables(this.databaseName).subscribe((response) => {
      this.tables = response;
      this.tablesName = response.map(table => table.tableName);
    })
    this.initForm();
  }

  onSelect() {

    console.log(this.selectForm)
    const projection = [];
    const conditions = [];
    const joinConditions = [];

    for (let i = 0; i < this.projectionItems.length; ++i) {
      const item = this.projectionItems.at(i).value;
      projection.push(item.attributeName);
    }

    this.attributesSelected = [...projection];

    for (let i = 0; i < this.conditionsItems.length; ++i) {
      const item = this.conditionsItems.at(i).value;
      conditions.push({
        columnName: item.tableName + '.' + item.columnName,
        value: item.value,
        operation: item.operation
      });
    }

    for (let i = 0; i < this.joinItems.length; ++i) {
      const item = this.joinItems.at(i).value;
      joinConditions.push({
        columnName: item.attribute,
        tableName: item.table
      })
    }

    const query = {
      projection,
      conditions,
      joinConditions,
      isDistinct: this.selectForm.get('isDistinct').value,
      initialTableName: this.tableName
    };

    console.log(query)
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

  get joinItems() {
    return this.selectForm.get('join') as FormArray;
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

  addJoin() {
    this.joinItems.push(
      this.fb.group({
        table: ['', Validators.required],
        attribute: ['', Validators.required]
      })
    )

    const length = this.joinItems.controls.length;
    const control = this.joinItems.controls[length - 1];
    control.get('table').valueChanges.subscribe(value => {
      const tableSelectedJoin = this.tables.find(table => table.tableName === value);
      this.attributesJoin[length - 1] = tableSelectedJoin.attributes.map(attr => attr.attributeName);
    })
  }

  deleteCondition(index) {
    this.conditionsItems.removeAt(index);
  }

  deleteJoin(index) {
    this.joinItems.removeAt(index);
  }

  addCondition() {
    this.conditionsItems.push(
      this.fb.group({
        tableName: ['', Validators.required],
        columnName: ['', Validators.required],
        operation: [this.operations[0], Validators.required],
        value: ['', Validators.required],
      })
    );

    const length = this.conditionsItems.controls.length;
    const control = this.conditionsItems.controls[length - 1];
    control.get('tableName').valueChanges.subscribe(value => {
      const tableSelectedCondition = this.tables.find(table => table.tableName === value);
      this.attributesCondition[length - 1] = tableSelectedCondition.attributes.map(attr => attr.attributeName);
    })
  }

  initForm() {
    this.selectForm = this.fb.group({
      isDistinct: false,
      projection: this.fb.array([
        this.fb.group({
          attributeName: [this.attributesProjection[0], Validators.required],
        }),
      ]),
      conditions: this.fb.array([]),
      join: this.fb.array([])
    });
  }
}
