import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Index } from '../../models/databases-response.model';

@Component({
  selector: 'ado-create-index-modal',
  templateUrl: './create-index-modal.component.html',
  styleUrls: ['./create-index-modal.component.scss']
})
export class CreateIndexModalComponent implements OnInit {

  @Output() indexToSave = new EventEmitter<Index>();
  indexForm: FormGroup;
  
  constructor(private fb: FormBuilder,  private activeModalService: NgbActiveModal) { }

  ngOnInit() {
    this.indexForm = this.fb.group({
      indexName: ['', Validators.required],
      isUnique: [false, Validators.required],
      indexAttributes: ['', Validators.required]
    });
  }

  onClose() {
    this.activeModalService.close();
  }

  onSaveIndex() {
    const value = this.indexForm.value;
    const index: Index = {
      indexName: value.indexName.toString(),
      isUnique: value.isUnique ? 1 : 0,
      indexAttributes: value.indexAttributes.split(" ")
    }

    this.indexToSave.emit(index);
    this.onClose();
  }

}
