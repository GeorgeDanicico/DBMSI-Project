/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TablesListComponent } from './tables-list.component';

describe('TablesListComponent', () => {
  let component: TablesListComponent;
  let fixture: ComponentFixture<TablesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TablesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TablesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
