/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { DatabasesListComponent } from './databases-list.component';

describe('DatabasesListComponent', () => {
  let component: DatabasesListComponent;
  let fixture: ComponentFixture<DatabasesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DatabasesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatabasesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
