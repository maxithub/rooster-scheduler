import { Component, OnInit } from '@angular/core';
import { Job, HttpJob, ShellJob } from '../job.model';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

export enum JobType {
  HTTP, Shell
}

@Component({
  selector: 'app-create-job',
  templateUrl: './create-job.component.html',
  styleUrls: ['./create-job.component.css']
})
export class CreateJobComponent implements OnInit {
  // name: string;
  // cronExpr: string;
  // type: JobType;
  // url: string;
  // host: string;
  // port: number;
  // path: string;

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
