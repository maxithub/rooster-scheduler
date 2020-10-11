import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-job',
  templateUrl: './create-job.component.html',
  styleUrls: ['./create-job.component.css']
})
export class CreateJobComponent implements OnInit {

  jobForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.jobForm = this.formBuilder.group({
      jobName: [null, [Validators.required]],
      cronExpr: [null, [Validators.required]]
    });
  }

  create(): void {
    console.log(this.jobForm.value);
  }
}
