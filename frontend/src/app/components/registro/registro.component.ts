import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.scss']
})
export class RegistroComponent {
  registroForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private router: Router
  ) {
    this.registroForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    if (confirmPassword?.errors?.['passwordMismatch']) {
      delete confirmPassword.errors['passwordMismatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.registroForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const { confirmPassword, ...registroData } = this.registroForm.value;

      this.usuarioService.registrar(registroData).subscribe({
        next: (usuario) => {
          console.log('Registro exitoso:', usuario);
          this.successMessage = '¡Registro exitoso! Redirigiendo al login...';
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (error) => {
          console.error('Error en registro:', error);
          this.errorMessage = error.error || 'Error al registrar usuario';
          this.isLoading = false;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registroForm.controls).forEach(key => {
      const control = this.registroForm.get(key);
      control?.markAsTouched();
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.registroForm.get(controlName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) {
        return `${this.getFieldName(controlName)} es obligatorio`;
      }
      if (control.errors['email']) {
        return 'El email no es válido';
      }
      if (control.errors['minlength']) {
        const minLength = control.errors['minlength'].requiredLength;
        return `${this.getFieldName(controlName)} debe tener al menos ${minLength} caracteres`;
      }
      if (control.errors['passwordMismatch']) {
        return 'Las contraseñas no coinciden';
      }
    }
    return '';
  }

  private getFieldName(controlName: string): string {
    const fieldNames: { [key: string]: string } = {
      nombre: 'El nombre',
      email: 'El email',
      password: 'La contraseña',
      confirmPassword: 'La confirmación de contraseña'
    };
    return fieldNames[controlName] || controlName;
  }
} 