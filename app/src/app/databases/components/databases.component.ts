import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { switchMap } from 'rxjs/operators';
import { DatabaseResponse } from '../models/databases-response.model';
import { DatabaseService } from '../services/database.service';
import { ToastService } from 'src/app/shared/components/toasts-container/toasts-service';

@Component({
  selector: 'ado-databases',
  templateUrl: './databases.component.html',
  styleUrls: ['./databases.component.scss'],
})
export class DatabasesComponent implements OnInit {
  databases: DatabaseResponse[] = [];
  databaseSelected: DatabaseResponse;
  form = new FormGroup({
    name: new FormControl('', Validators.required),
  });

  constructor(private databaseService: DatabaseService, private toastService: ToastService) {}

  ngOnInit() {
    this.databaseService.getDatabases().subscribe((response) => {
      this.databases = response;
    });
  }

  onNewDatabase(): void {
    this.databaseService
      .saveDatabase(this.form.get('name').value)
      .pipe(
        switchMap(() => {
          return this.databaseService.getDatabases();
        })
      )
      .subscribe((response) => {
        this.databases = response;
        this.databaseSelected = null;
        this.toastService.showSuccess("Database created successfully")
      }, () => {
        this.toastService.showError("Could not create database");
      });
  }

  onDatabaseSelected(database: DatabaseResponse) {
    this.databaseSelected = database;
  }

  onDatabaseDeleted(database: DatabaseResponse) {
    this.databaseService
      .deleteDatabase(database.databaseName)
      .pipe(
        switchMap(() => {
          return this.databaseService.getDatabases();
        })
      )
      .subscribe((response) => {
        this.databases = response;
        this.databaseSelected = null;
        this.toastService.showSuccess("Database deleted successfully")
      }, () => {
        this.toastService.showError("Could not delete database");
      });
  }
}
